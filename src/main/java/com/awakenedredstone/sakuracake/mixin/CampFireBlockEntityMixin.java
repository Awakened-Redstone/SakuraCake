package com.awakenedredstone.sakuracake.mixin;

import com.awakenedredstone.sakuracake.registry.CherryBlocks;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CampfireBlockEntity.class)
public class CampFireBlockEntityMixin {

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
}
