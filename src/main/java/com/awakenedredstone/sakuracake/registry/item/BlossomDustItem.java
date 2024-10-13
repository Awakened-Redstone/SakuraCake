package com.awakenedredstone.sakuracake.registry.item;

import com.awakenedredstone.sakuracake.registry.CherryBlocks;
import com.awakenedredstone.sakuracake.registry.block.CherryCauldronBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.spawner.TrialSpawnerLogic;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlossomDustItem extends Item {
    public BlossomDustItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();

        if (!(world instanceof ServerWorld serverWorld)) {
            return ActionResult.PASS;
        }

        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);

        if (state.isOf(Blocks.CAULDRON)) {
            /*for (float x = -0.1f; x < 1.1f; x += 0.1f) {
                for (float z = -0.1f; z < 1.1f; z += 0.1f) {
                    serverWorld.spawnParticles(ParticleTypes.TRIAL_SPAWNER_DETECTION, pos.getX() + x, pos.getY(), pos.getZ() + z, 1, 0, 1, 0, 0);
                }
            }*/

            //spawnParticles(world, pos, ParticleTypes.TRIAL_SPAWNER_DETECTION);
            world.setBlockState(pos, CherryBlocks.CAULDRON.getDefaultState());

            TrialSpawnerLogic.addDetectionParticles(world, pos, world.getRandom(), 10, ParticleTypes.TRIAL_SPAWNER_DETECTION);
            return ActionResult.SUCCESS;
        }

        return super.useOnBlock(context);
    }

    public static void spawnParticles(World world, BlockPos pos, ParticleEffect particle) {
        if (world.isClient()) return;
        ServerWorld serverWorld = (ServerWorld) world;

        for (float i = 0; i < 1.2; i += 0.05f) {
            for (float y = 0; y < 1.1; y += 0.2f) {
                serverWorld.spawnParticles(particle, pos.getX() + i - 0.05, pos.getY() + y, pos.getZ() - 0.1, 1, 0, 0, 0, 0);
            }
        }

        for (float i = 0; i < 1.2; i += 0.05f) {
            for (float y = 0; y < 1.1; y += 0.2f) {
                serverWorld.spawnParticles(particle, pos.getX() + i - 0.05, pos.getY() + y, pos.getZ() + 1.1, 1, 0, 0, 0, 0);
            }
        }

        for (float i = 0; i < 1.2; i += 0.05f) {
            for (float y = 0; y < 1.1; y += 0.2f) {
                serverWorld.spawnParticles(particle, pos.getX() - 0.1, pos.getY() + y, pos.getZ() + i - 0.05, 1, 0, 0, 0, 0);
            }
        }

        for (float i = 0; i < 1.2; i += 0.05f) {
            for (float y = 0; y < 1.1; y += 0.2f) {
                serverWorld.spawnParticles(particle, pos.getX() + 1.1, pos.getY() + y, pos.getZ() + i - 0.05, 1, 0, 0, 0, 0);
            }
        }
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return super.canMine(state, world, pos, miner);
    }
}
