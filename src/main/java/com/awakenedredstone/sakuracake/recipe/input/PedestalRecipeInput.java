package com.awakenedredstone.sakuracake.recipe.input;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.recipe.input.RecipeInput;

import java.util.List;

public class PedestalRecipeInput implements RecipeInput {
    private final RecipeMatcher matcher = new RecipeMatcher();
    private final List<ItemStack> stacks;
    private final ItemStack key;
    private final int stackCount;

    public PedestalRecipeInput(ItemStack key, List<ItemStack> stacks) {
        if (key.isEmpty()) {
            throw new UnsupportedOperationException("The key item can not be empty");
        }

        this.key = key;
        this.stacks = stacks;

        this.matcher.addInput(key, 1);

        int i = 1;
        for (ItemStack itemStack : stacks) {
            if (!itemStack.isEmpty()) {
                i++;
                this.matcher.addInput(itemStack, 1);
            }
        }

        this.stackCount = i;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return slot == 0 ? key : stacks.get(slot - 1);
    }

    @Override
    public int getSize() {
        return stacks.size() + 1;
    }

    public RecipeMatcher getRecipeMatcher() {
        return matcher;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof PedestalRecipeInput recipeInput) {
            return this.stackCount == recipeInput.stackCount
                   && ItemStack.areEqual(this.key, recipeInput.key)
                   && ItemStack.stacksEqual(this.stacks, recipeInput.stacks);
        }
        return false;
    }

    public int getStackCount() {
        return this.stackCount;
    }
}
