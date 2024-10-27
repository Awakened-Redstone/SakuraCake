package com.awakenedredstone.sakuracake.recipe;

import com.awakenedredstone.sakuracake.recipe.input.ItemStackRecipeInput;
import com.awakenedredstone.sakuracake.registry.CherryBlocks;
import com.awakenedredstone.sakuracake.registry.CherryRecipeSerializers;
import com.awakenedredstone.sakuracake.registry.CherryRecipeTypes;
import com.awakenedredstone.sakuracake.util.Cast;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public record MixinRecipe(ItemStack result, DefaultedList<Ingredient> ingredients) implements Recipe<ItemStackRecipeInput> {
    private static final ItemStack ICON = new ItemStack(CherryBlocks.CAULDRON);
    //private final String group;
    //private final CraftingRecipeCategory category;

    @Override
    public ItemStack createIcon() {
        return ICON;
    }

    @Override
    public boolean matches(ItemStackRecipeInput input, World world) {
        if (input.getStackCount() != this.ingredients.size()) {
            return false;
        }

        if (input.getSize() == 1 && this.ingredients.size() == 1) {
            return this.ingredients.getFirst().test(input.getStackInSlot(0));
        }

        return input.getRecipeMatcher().match(this, null);
    }

    @Override
    public ItemStack craft(ItemStackRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return this.result.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true; //TODO: Calculate
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return this.result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CherryRecipeSerializers.MIXIN;
    }

    @Override
    public RecipeType<?> getType() {
        return CherryRecipeTypes.MIXIN;
    }
}
