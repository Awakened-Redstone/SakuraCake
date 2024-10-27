package com.awakenedredstone.sakuracake.registry;

import com.awakenedredstone.sakuracake.SakuraCake;
import com.awakenedredstone.sakuracake.internal.registry.AutoRegistry;
import com.awakenedredstone.sakuracake.internal.registry.RegistryNamespace;
import com.awakenedredstone.sakuracake.recipe.MixinRecipe;
import com.awakenedredstone.sakuracake.recipe.ThaumicRecipe;
import com.awakenedredstone.sakuracake.recipe.serializer.MixinRecipeSerializer;
import com.awakenedredstone.sakuracake.recipe.serializer.ThaumicRecipeSerializer;
import com.awakenedredstone.sakuracake.util.Cast;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

@RegistryNamespace(SakuraCake.MOD_ID)
public class CherryRecipeSerializers implements AutoRegistry<RecipeSerializer<?>> {
    public static final RecipeSerializer<MixinRecipe> MIXIN = new MixinRecipeSerializer();
    public static final RecipeSerializer<ThaumicRecipe> THAUMIC = new ThaumicRecipeSerializer();

    @Override
    public Registry<RecipeSerializer<?>> registry() {
        return Registries.RECIPE_SERIALIZER;
    }

    @Override
    public Class<RecipeSerializer<?>> fieldType() {
        return Cast.conform(RecipeSerializer.class);
    }
}
