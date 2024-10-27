package com.awakenedredstone.sakuracake.client.mixin;

import com.awakenedredstone.sakuracake.SakuraCake;
import com.awakenedredstone.sakuracake.client.event.RenderLayerRegistrationCallback;
import com.awakenedredstone.sakuracake.client.render.CherryRenderLayers;
import com.awakenedredstone.sakuracake.registry.CherryBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;

@Mixin(RenderLayers.class)
public class RenderLayersMixin {
    @Inject(method = "method_23685", at = @At("TAIL"))
    private static void addEvent(HashMap<Block, RenderLayer> map, CallbackInfo ci) {
        RenderLayerRegistrationCallback.EVENT.invoker().register(map);
    }

    @Inject(method = {"getBlockLayer", "getMovingBlockLayer"}, at = @At(value = "RETURN"), cancellable = true)
    private static void makeCauldronBlockRenderProperlyOnDifferentConditions(BlockState state, CallbackInfoReturnable<RenderLayer> cir) {
        if (state.isOf(CherryBlocks.CAULDRON)) {
            if (SakuraCake.isModLoaded("sodium")) {
                cir.setReturnValue(RenderLayer.getTranslucent());
            } else if (MinecraftClient.getInstance().options.getGraphicsMode().getValue().getId() >= GraphicsMode.FABULOUS.getId()) {
                cir.setReturnValue(CherryRenderLayers.CUTOUT_MIPPED_TRANSLUCENT);
            } else {
                cir.setReturnValue(RenderLayer.getCutoutMipped());
            }
        }
    }
}
