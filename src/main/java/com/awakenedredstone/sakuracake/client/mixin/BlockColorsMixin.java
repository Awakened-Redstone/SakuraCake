package com.awakenedredstone.sakuracake.client.mixin;

import com.awakenedredstone.sakuracake.registry.CherryBlocks;
import com.llamalad7.mixinextras.lib.apache.commons.ArrayUtils;
import net.minecraft.block.Block;
import net.minecraft.client.color.block.BlockColors;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//? if fabric {
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
//?} else {
/*import org.spongepowered.asm.mixin.injection.ModifyArg;
*///?}

@Mixin(BlockColors.class)
public class BlockColorsMixin {
    //? if fabric {
    @Definition(id = "Block", type = Block.class)
    @Definition(id = "WATER", field = "Lnet/minecraft/block/Blocks;WATER:Lnet/minecraft/block/Block;")
    @Definition(id = "BUBBLE_COLUMN", field = "Lnet/minecraft/block/Blocks;BUBBLE_COLUMN:Lnet/minecraft/block/Block;")
    @Definition(id = "WATER_CAULDRON", field = "Lnet/minecraft/block/Blocks;WATER_CAULDRON:Lnet/minecraft/block/Block;")
    @Expression("new Block[]{WATER, BUBBLE_COLUMN, WATER_CAULDRON}")
    @ModifyExpressionValue(method = "create", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static Block[] addCauldronColor(Block[] original) {
        return ArrayUtils.add(original, CherryBlocks.CAULDRON);
    }
    //?} else {
    /*@ModifyArg(method = "create", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/color/block/BlockColors;registerColorProvider(Lnet/minecraft/client/color/block/BlockColorProvider;[Lnet/minecraft/block/Block;)V", ordinal = 6), index = 1)
    private static Block[] addCauldronColor(Block[] original) {
        return ArrayUtils.add(original, CherryBlocks.CAULDRON);
    }
    *///?}
}
