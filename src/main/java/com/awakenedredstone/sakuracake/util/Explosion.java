package com.awakenedredstone.sakuracake.util;

import com.google.common.collect.Lists;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class Explosion {
    public static void createExplosion(Entity entity, World world, BlockPos center, float power) {
        drawSphere(entity, world, center, power, false);
        drawSphere(entity, world, center, power + 3, true);
    }

    public static Optional<Float> getBlastResistance(BlockState blockState, FluidState fluidState) {
        if (blockState.isAir() && fluidState.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(Math.max(blockState.getBlock().getBlastResistance(), fluidState.getBlastResistance()));
    }

    private static int setBlock(World world, BlockPos.Mutable pos, int x, int y, int z, BlockState block, List<BlockPos> list, float power) {
        pos.set(x, y, z);
        int success = 0;
        float blastResistance = getBlastResistance(world.getBlockState(pos), world.getFluidState(pos)).orElse(-1.0f);
        if (blastResistance >= 0 && blastResistance <= power) {
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof Inventory) {
                ((Inventory) tileEntity).clear();
            }
            if (world.setBlockState(pos, block)) {
                list.add(pos.toImmutable());
                ++success;
            }
        }

        return success;
    }

    private static double lengthSq(double x, double y, double z) {
        return (x * x) + (y * y) + (z * z);
    }

    public static int drawSphere(Entity entity, World world, BlockPos center, float power, boolean random) {
        BlockState block = Blocks.AIR.getDefaultState();

        int affected = 0;

        double radiusX = power * 2.132 + 0.5;
        double radiusY = power * 2.132 + 0.5;
        double radiusZ = power * 2.132 + 0.5;

        final double invRadiusX = 1 / radiusX;
        final double invRadiusY = 1 / radiusY;
        final double invRadiusZ = 1 / radiusZ;

        final int ceilRadiusX = (int) Math.ceil(radiusX);
        final int ceilRadiusY = (int) Math.ceil(radiusY);
        final int ceilRadiusZ = (int) Math.ceil(radiusZ);

        BlockPos.Mutable mbpos = center.mutableCopy();
        List<BlockPos> list = Lists.newArrayList();

        double nextXn = 0;

        forX: for (int x = 0; x <= ceilRadiusX; ++x) {
            final double xn = nextXn;
            nextXn = (x + 1) * invRadiusX;
            double nextYn = 0;
            forY: for (int y = 0; y <= ceilRadiusY; ++y) {
                final double yn = nextYn;
                nextYn = (y + 1) * invRadiusY;
                double nextZn = 0;
                forZ: for (int z = 0; z <= ceilRadiusZ; ++z) {
                    final double zn = nextZn;
                    nextZn = (z + 1) * invRadiusZ;

                    double distanceSq = lengthSq(xn, yn, zn);
                    if (distanceSq > 1) {
                        if (z == 0) {
                            if (y == 0) {
                                break forX;
                            }
                            break forY;
                        }
                        break forZ;
                    }

                    if (random) {
                        if ((new Random().nextInt(100) + 1) > 30) {
                            continue forZ;
                        }
                    }

                    for (int xmod = -1; xmod < 2; xmod += 2) {
                        for (int ymod = -1; ymod < 2; ymod += 2) {
                            for (int zmod = -1; zmod < 2; zmod += 2) {
                                BlockPos pos = new BlockPos(center.getX() + xmod * x, center.getY() + ymod * y, center.getZ() + zmod * z);
                                affected += setBlock(world, mbpos, pos.getX(), pos.getY(), pos.getZ(), block, list, power);
                            }
                        }
                    }
                }
            }
        }
        return affected;
    }

}
