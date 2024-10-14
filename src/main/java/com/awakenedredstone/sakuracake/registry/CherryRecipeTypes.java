package com.awakenedredstone.sakuracake.registry;

import com.awakenedredstone.sakuracake.internal.registry.AutoRegistry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class CherryRecipeTypes implements AutoRegistry<RecipeType<?>> {
    @Override
    public Registry<RecipeType<?>> registry() {
        return Registries.RECIPE_TYPE;
    }

    @Override
    public Class<RecipeType<?>> fieldType() {
        return AutoRegistry.conform(RecipeType.class);
    }
}
