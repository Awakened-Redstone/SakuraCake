package com.awakenedredstone.sakuracake.registry.block.entity;

import com.awakenedredstone.sakuracake.registry.CherryBlockEntities;
import com.awakenedredstone.sakuracake.registry.CherryParticles;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FocusedAuraBlockEntity extends BlockEntity {
    public FocusedAuraBlockEntity(BlockPos pos, BlockState state) {
        super(CherryBlockEntities.FOCUSED_AURA, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, FocusedAuraBlockEntity blockEntity) {
        if (world instanceof ClientWorld clientWorld) {
            clientWorld.addImportantParticle(CherryParticles.SPARKLE_PARTICLE, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, 0, 0, 0);
        }
    }
}
