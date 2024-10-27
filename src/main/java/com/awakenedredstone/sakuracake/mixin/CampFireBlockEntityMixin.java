package com.awakenedredstone.sakuracake.mixin;

import com.awakenedredstone.sakuracake.registry.CherryBlocks;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//? if fabric {
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import net.minecraft.util.math.random.Random;
//?}

@Mixin(CampfireBlockEntity.class)
public class CampFireBlockEntityMixin {
    //? if fabric {
    @Environment(EnvType.CLIENT)
    @Definition(id = "random", local = @Local(type = Random.class))
    @Definition(id = "nextFloat", method = "Lnet/minecraft/util/math/random/Random;nextFloat()F")
    @Expression("random.nextFloat() < 0.11")
    @ModifyExpressionValue(method = "clientTick", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static boolean removeSmokeOnCauldron(boolean original, @Local(argsOnly = true) World world, @Local(argsOnly = true) BlockPos pos) {
        if (original && world.getBlockState(pos.offset(Direction.UP, 1)).isOf(CherryBlocks.CAULDRON)) {
            return false;
        }

        return original;
    }
    //?} else {
    /*@Environment(EnvType.CLIENT)
    @ModifyExpressionValue(method = "clientTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/random/Random;nextFloat()F", ordinal = 0))
    private static float removeSmokeOnCauldron(float original, @Local(argsOnly = true) World world, @Local(argsOnly = true) BlockPos pos) {
        if (original < 0.11F && world.getBlockState(pos.offset(Direction.UP, 1)).isOf(CherryBlocks.CAULDRON)) {
            return 1;
        }

        return original;
    }
    *///?}
}
