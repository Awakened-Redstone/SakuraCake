package com.awakenedredstone.sakuracake.client.mixin;

import com.awakenedredstone.sakuracake.client.event.RenderLayerRegistrationCallback;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;

@Mixin(RenderLayers.class)
public class RenderLayersMixin {
    @Inject(method = "method_23685", at = @At("TAIL"))
    private static void funnyEvent(HashMap<Block, RenderLayer> map, CallbackInfo ci) {
        RenderLayerRegistrationCallback.EVENT.invoker().register(map);
    }

    /*@Definition(id = "block", local = @Local(type = Block.class))
    @Definition(id = "LeavesBlock", type = LeavesBlock.class)
    @Expression("block instanceof LeavesBlock")
    @ModifyExpressionValue(method = {"getBlockLayer", "getMovingBlockLayer"}, at = @At("MIXINEXTRAS:EXPRESSION"))
    private static boolean improveCutoutRenderer1(boolean original, @Local Block block) {
        return original || block instanceof CutoutRenderer;
    }*/
}
