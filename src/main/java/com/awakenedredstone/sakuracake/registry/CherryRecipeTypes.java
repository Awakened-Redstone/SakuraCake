package com.awakenedredstone.sakuracake.registry;

import com.awakenedredstone.sakuracake.SakuraCake;
import com.awakenedredstone.sakuracake.internal.registry.AutoRegistry;
import com.awakenedredstone.sakuracake.internal.registry.RegistryNamespace;
import com.awakenedredstone.sakuracake.recipe.MixinRecipe;
import com.awakenedredstone.sakuracake.recipe.ThaumicRecipe;
import com.awakenedredstone.sakuracake.util.Cast;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

@RegistryNamespace(SakuraCake.MOD_ID)
public class CherryRecipeTypes implements AutoRegistry<RecipeType<?>> {
    public static final RecipeType<MixinRecipe> MIXIN = create("mixin");
    public static final RecipeType<ThaumicRecipe> THAUMIC = create("thaumic");

    private static <T extends Recipe<?>> RecipeType<T> create(String id) {
        return new RecipeType<>() {
            public String toString() {
                return SakuraCake.id(id).toString();
            }
        };
    }

    @Override
    public Registry<RecipeType<?>> registry() {
        return Registries.RECIPE_TYPE;
    }

    @Override
    public Class<RecipeType<?>> fieldType() {
        return Cast.conform(RecipeType.class);
    }
}
