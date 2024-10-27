package com.awakenedredstone.sakuracake.recipe;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class StackIngredient extends Ingredient {
    private static final Map<Class<? extends Entry>, Identifier> ENTRY_IDS = new HashMap<>();
    private static final Map<Identifier, MapCodec<? extends Entry>> ENTRIES = new HashMap<>();
    private static final Codec<Entry> ENTRY_CODEC = Identifier.CODEC.dispatch(entry -> ENTRY_IDS.get(entry.getClass()), ENTRIES::get);

    public static final PacketCodec<RegistryByteBuf, StackIngredient> PACKET_CODEC = ItemStack.LIST_PACKET_CODEC.xmap(list -> StackIngredient.ofEntries(list.stream().map(StackEntry::new)), ingredient -> Arrays.asList(ingredient.getMatchingStacks()));
    public static final StackIngredient EMPTY = new StackIngredient(Stream.empty());
    public static final Codec<StackIngredient> ALLOW_EMPTY_CODEC = createCodec(true);
    public static final Codec<StackIngredient> DISALLOW_EMPTY_CODEC = createCodec(false);

    private StackIngredient(Entry[] entries) {
        super(entries);
    }

    private StackIngredient(Stream<? extends Entry> entries) {
        super(entries);
    }

    /*? if neoforge {*/public/*?} else {*//*private*//*?}*/ static StackIngredient ofEntries(Stream<? extends Entry> entries) {
        StackIngredient ingredient = new StackIngredient(entries);
        return ingredient.isEmpty() ? EMPTY : ingredient;
    }

    private static Codec<StackIngredient> createCodec(boolean allowEmpty) {
        Codec<Entry[]> codec = Codec.list(ENTRY_CODEC).comapFlatMap((entries) -> !allowEmpty && entries.isEmpty() ?
          DataResult.error(() -> "Item array cannot be empty, at least one item must be defined")
          : DataResult.success(entries.toArray(new Entry[0])), List::of);

        return Codec.either(codec, ENTRY_CODEC).flatComapMap(
          (either) -> either.map(StackIngredient::new, (entry) -> new StackIngredient(new Entry[]{entry})), ingredient -> {
              if (ingredient.entries.length == 1) {
                  return DataResult.success(Either.right(ingredient.entries[0]));
              } else {
                  return ingredient.entries.length == 0 && !allowEmpty ?
                    DataResult.error(() -> "Item array cannot be empty, at least one item must be defined")
                    : DataResult.success(Either.left(ingredient.entries));
              }
          });
    }

    @Override
    public boolean test(@Nullable ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }

        if (this.isEmpty()) {
            return itemStack.isEmpty();
        }

        for (ItemStack itemStack2 : this.getMatchingStacks()) {
            if (!ItemStack.areEqual(itemStack2, itemStack)) continue;
            return true;
        }

        return false;
    }

    public static <T extends Entry> void registerEntry(Class<T> clazz, Identifier id, MapCodec<T> codec) {
        if (ENTRIES.containsKey(id)) {
            throw new IllegalStateException("Entry " + id + " already registered!");
        }

        ENTRIES.put(id, codec);
        ENTRY_IDS.put(clazz, id);
    }

    static {
        MapCodec<Ingredient.StackEntry> itemCodec = RecordCodecBuilder.mapCodec((instance) ->
          instance.group(
            ItemStack.REGISTRY_ENTRY_CODEC.fieldOf("item").forGetter(Ingredient.StackEntry::stack)
          ).apply(instance, Ingredient.StackEntry::new)
        );
        MapCodec<Ingredient.TagEntry> tagCodec = RecordCodecBuilder.mapCodec((instance) ->
          instance.group(
            TagKey.unprefixedCodec(RegistryKeys.ITEM).fieldOf("tag").forGetter(TagEntry::tag)
          ).apply(instance, TagEntry::new));

        registerEntry(StackEntry.class, StackEntry.ID, StackEntry.CODEC);
        registerEntry(Ingredient.StackEntry.class, Identifier.ofVanilla("item"), itemCodec);
        registerEntry(TagEntry.class, Identifier.ofVanilla("tag"), tagCodec);
    }

    protected record StackEntry(ItemStack stack) implements Entry {
        public static final Identifier ID = Identifier.ofVanilla("stack");
        public static final MapCodec<StackEntry> CODEC = RecordCodecBuilder.mapCodec((instance) ->
          instance.group(
            ItemStack.CODEC.fieldOf("item").forGetter(StackEntry::stack)
          ).apply(instance, StackEntry::new)
        );

        public Collection<ItemStack> getStacks() {
            return Collections.singleton(this.stack);
        }

        public ItemStack stack() {
            return this.stack;
        }
    }
}
