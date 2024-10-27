package com.awakenedredstone.sakuracake.recipe.serializer;

import com.awakenedredstone.sakuracake.recipe.ThaumicRecipe;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.collection.DefaultedList;

public class ThaumicRecipeSerializer implements RecipeSerializer<ThaumicRecipe> {
    public static final MapCodec<ThaumicRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
      instance.group(
        ItemStack.CODEC.fieldOf("result").forGetter(ThaumicRecipe::result),
        Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("key").forGetter(ThaumicRecipe::key),
        Ingredient.DISALLOW_EMPTY_CODEC.listOf().fieldOf("ingredients").flatXmap(ingredients -> {
            Ingredient[] ingredients2 = ingredients.stream().filter(ingredient -> !ingredient.isEmpty()).toArray(Ingredient[]::new);
            if (ingredients2.length == 0) {
                return DataResult.error(() -> "No ingredients for Thaumic recipe");
            }
            if (ingredients2.length > 8) {
                return DataResult.error(() -> "Too many ingredients for Thaumic recipe");
            }
            return DataResult.success(DefaultedList.copyOf(Ingredient.EMPTY, ingredients2));
        }, DataResult::success).forGetter(ThaumicRecipe::ingredients)
      ).apply(instance, ThaumicRecipe::new)
    );
    public static final PacketCodec<RegistryByteBuf, ThaumicRecipe> PACKET_CODEC = PacketCodec.ofStatic(ThaumicRecipeSerializer::write, ThaumicRecipeSerializer::read);

    @Override
    public MapCodec<ThaumicRecipe> codec() {
        return CODEC;
    }

    @Override
    public PacketCodec<RegistryByteBuf, ThaumicRecipe> packetCodec() {
        return PACKET_CODEC;
    }

    private static ThaumicRecipe read(RegistryByteBuf buf) {
        ItemStack result = ItemStack.PACKET_CODEC.decode(buf);

        Ingredient key = Ingredient.PACKET_CODEC.decode(buf);

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

        return new ThaumicRecipe(result, key, ingredients);
    }

    private static void write(RegistryByteBuf buf, ThaumicRecipe recipe) {
        ItemStack.PACKET_CODEC.encode(buf, recipe.result());

        Ingredient.PACKET_CODEC.encode(buf, recipe.key());

        buf.writeByte(recipe.ingredients().size());
        for (Ingredient ingredient : recipe.ingredients()) {
            Ingredient.PACKET_CODEC.encode(buf, ingredient);
        }
    }
}
