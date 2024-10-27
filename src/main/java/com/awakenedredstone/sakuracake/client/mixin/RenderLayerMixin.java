package com.awakenedredstone.sakuracake.client.mixin;

import com.awakenedredstone.sakuracake.client.render.CherryRenderLayers;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.render.RenderLayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderLayer.class)
public class RenderLayerMixin {
    @Shadow @Mutable @Final private static ImmutableList<RenderLayer> BLOCK_LAYERS;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void addLayers(CallbackInfo ci) {
        ImmutableList.Builder<RenderLayer> builder = ImmutableList.builder();
        builder.addAll(BLOCK_LAYERS);
        builder.add(CherryRenderLayers.CUTOUT_MIPPED_TRANSLUCENT);

        BLOCK_LAYERS = builder.build();
    }
}
