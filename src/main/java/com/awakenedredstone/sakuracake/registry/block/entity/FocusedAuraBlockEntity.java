package com.awakenedredstone.sakuracake.registry.block.entity;

import com.awakenedredstone.sakuracake.registry.CherryBlockEntities;
import com.awakenedredstone.sakuracake.registry.CherryParticles;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FocusedAuraBlockEntity extends BlockEntity {
    private final RecipeManager.MatchGetter<SingleStackRecipeInput, CampfireCookingRecipe> matchGetter = RecipeManager.createCachedMatchGetter(RecipeType.CAMPFIRE_COOKING);

    public FocusedAuraBlockEntity(BlockPos pos, BlockState state) {
        super(CherryBlockEntities.FOCUSED_AURA, pos, state);
    }

    public static void tickClient(World world, BlockPos pos, BlockState state, FocusedAuraBlockEntity block) {
        if (block.removed) return;
        world.addImportantParticle(CherryParticles.FOCUSED_AURA, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, 0, 0, 0);
    }
}
