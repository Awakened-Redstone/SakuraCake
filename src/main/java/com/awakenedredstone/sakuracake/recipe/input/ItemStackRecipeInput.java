package com.awakenedredstone.sakuracake.recipe.input;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.recipe.input.RecipeInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ItemStackRecipeInput implements RecipeInput {
    public static final Logger LOGGER = LoggerFactory.getLogger("ItemStack Recipe Input");
    public static final ItemStackRecipeInput EMPTY = new ItemStackRecipeInput(List.of());
    private final List<ItemStack> stacks;
    private final RecipeMatcher matcher = new RecipeMatcher();
    private final int stackCount;

    public ItemStackRecipeInput(List<ItemStack> stacks) {
        int i = 0;

        List<ItemStack> items = new ArrayList<>(stacks.size());
        List<ItemStack> processedStacks = new ArrayList<>(stacks.size());

        for (ItemStack stack : stacks) {
            if (stack.isEmpty()) continue;
            ItemStack reducedStack = stack.copyWithCount(1);
            if (items.contains(reducedStack)) {
                ItemStack match = processedStacks.get(items.lastIndexOf(reducedStack));

                if (match.getCount() < match.getMaxCount()) {
                    int total = match.getCount() + stack.getCount();

                    if (total > match.getMaxCount() * 2) {
                        // If you manage to trigger this you should not be using whatever mod allowed you to do that in production
                        throw new IllegalStateException("Got 2 stacks that when summed together returned more than double the maximum stack size, can not take an overflowing stack!");
                    }

                    int newCount = Math.min(total, match.getMaxCount());
                    match.setCount(newCount);
                    stack.setCount(total - newCount);

                    if (match.isEmpty()) continue;
                }
            }

            items.add(reducedStack);
            processedStacks.add(stack);
        }

        this.stacks = processedStacks;

        // List is not needed anymore
        items.clear();

        for (ItemStack stack : processedStacks) {
            if (stack.isEmpty()) continue;
            ++i;
            this.matcher.addInput(stack);
        }
        this.stackCount = i;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return stacks.get(slot);
    }

    @Override
    public int getSize() {
        return this.stacks.size();
    }

    @Override
    public boolean isEmpty() {
        return this.stackCount == 0;
    }

    public RecipeMatcher getRecipeMatcher() {
        return this.matcher;
    }

    public List<ItemStack> getStacks() {
        return this.stacks;
    }

    public int getStackCount() {
        return this.stackCount;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof ItemStackRecipeInput recipeInput) {
            return this.stackCount == recipeInput.stackCount && ItemStack.stacksEqual(this.stacks, recipeInput.stacks);
        }
        return false;
    }

    public int hashCode() {
        return ItemStack.listHashCode(this.stacks);
    }
}
