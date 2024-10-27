package com.awakenedredstone.sakuracake.client.mixin;

import com.awakenedredstone.sakuracake.event.WorldEvents;
import com.awakenedredstone.sakuracake.client.render.CherryRenderLayers;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
    @Shadow protected abstract void renderLayer(RenderLayer renderLayer, double x, double y, double z, Matrix4f matrix4f, Matrix4f positionMatrix);
    @Shadow private ClientWorld world;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;renderLayer(Lnet/minecraft/client/render/RenderLayer;DDDLorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V", ordinal = 3))
    private void renderExtraLayer(RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci, @Local(ordinal = 0) double d0, @Local(ordinal = 1) double d1, @Local(ordinal = 2) double d2) {
        this.renderLayer(CherryRenderLayers.CUTOUT_MIPPED_TRANSLUCENT, d0, d1, d2, matrix4f, matrix4f2);
    }

    @Inject(method = "processWorldEvent", at = @At("TAIL"))
    private void customWorldEvents(int eventId, BlockPos pos, int data, CallbackInfo ci, @Local Random random) {
        WorldEvents.WorldEvent event = WorldEvents.getEvent(eventId);
        if (event != null) event.process(world, pos, data, random);
    }
}
