package com.awakenedredstone.sakuracake.recipe.serializer;

import com.awakenedredstone.sakuracake.recipe.MixinRecipe;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.collection.DefaultedList;

public class MixinRecipeSerializer implements RecipeSerializer<MixinRecipe> {
    public static final MapCodec<MixinRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
      instance.group(
        ItemStack.CODEC.fieldOf("result").forGetter(MixinRecipe::result),
        Ingredient.DISALLOW_EMPTY_CODEC.listOf().fieldOf("ingredients").flatXmap(ingredients -> {
            Ingredient[] ingredients2 = ingredients.stream().filter(ingredient -> !ingredient.isEmpty()).toArray(Ingredient[]::new);
            if (ingredients2.length == 0) {
                return DataResult.error(() -> "No ingredients for Mixin' recipe");
            }
            if (ingredients2.length > 16) {
                return DataResult.error(() -> "Too many ingredients for Mixin' recipe");
            }
            return DataResult.success(DefaultedList.copyOf(Ingredient.EMPTY, ingredients2));
        }, DataResult::success).forGetter(MixinRecipe::ingredients)
      ).apply(instance, MixinRecipe::new)
    );
    public static final PacketCodec<RegistryByteBuf, MixinRecipe> PACKET_CODEC = PacketCodec.ofStatic(MixinRecipeSerializer::write, MixinRecipeSerializer::read);

    @Override
    public MapCodec<MixinRecipe> codec() {
        return CODEC;
    }

    @Override
    public PacketCodec<RegistryByteBuf, MixinRecipe> packetCodec() {
        return PACKET_CODEC;
    }

    private static MixinRecipe read(RegistryByteBuf buf) {
        ItemStack result = ItemStack.PACKET_CODEC.decode(buf);

        byte ingredientCount = buf.readByte();
        DefaultedList<Ingredient> ingredients;
        if (ingredientCount == 0) {
            ingredients = DefaultedList.of();
        } else {
            ingredients = DefaultedList.ofSize(ingredientCount);
            for (int i = 0; i < ingredientCount; i++) {
                ingredients.add(Ingredient.PACKET_CODEC.decode(buf));
            }
        }
        return new MixinRecipe(result, ingredients);
    }

    private static void write(RegistryByteBuf buf, MixinRecipe recipe) {
        ItemStack.PACKET_CODEC.encode(buf, recipe.result());

        buf.writeByte(recipe.ingredients().size());
        for (Ingredient ingredient : recipe.ingredients()) {
            Ingredient.PACKET_CODEC.encode(buf, ingredient);
        }
    }
}
