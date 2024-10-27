package com.awakenedredstone.sakuracake.recipe;

import com.awakenedredstone.sakuracake.recipe.input.PedestalRecipeInput;
import com.awakenedredstone.sakuracake.registry.CherryItems;
import com.awakenedredstone.sakuracake.registry.CherryRecipeSerializers;
import com.awakenedredstone.sakuracake.registry.CherryRecipeTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public record ThaumicRecipe(ItemStack result, Ingredient key, DefaultedList<Ingredient> ingredients) implements Recipe<PedestalRecipeInput> {
    private static final ItemStack ICON = new ItemStack(CherryItems.CHERRY_BLOSSOM_CAKE);

    @Override
    public ItemStack createIcon() {
        return ICON;
    }

    @Override
    public boolean matches(PedestalRecipeInput input, World world) {
        if (input.getStackCount() != this.ingredients.size() + 1) {
            return false;
        }

        if (input.getSize() == 1 && this.ingredients.size() == 1) {
            return this.ingredients.getFirst().test(input.getStackInSlot(0));
        }

        return input.getRecipeMatcher().match(this, null);
    }

    @Override
    public ItemStack craft(PedestalRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return this.result.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true; //TODO: Calculate
    }

    /** The key is always the fist item on the list */
    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> list = DefaultedList.ofSize(ingredients.size() + 1, Ingredient.EMPTY);
        list.set(0, key);
        int i = 1;
        for (Ingredient ingredient : ingredients) {
            list.set(i++, ingredient);
        }
        return list;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return this.result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CherryRecipeSerializers.THAUMIC;
    }

    @Override
    public RecipeType<?> getType() {
        return CherryRecipeTypes.THAUMIC;
    }
}
