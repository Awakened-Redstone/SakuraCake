package com.awakenedredstone.sakuracake.client.mixin;

import com.awakenedredstone.sakuracake.client.render.CherryParticleTextureSheets;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleTextureSheet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ParticleManager.class)
public class ParticleManagerMixin {
    @Shadow @Mutable @Final private static List<ParticleTextureSheet> PARTICLE_TEXTURE_SHEETS;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void registerSheets(CallbackInfo ci) {
        ImmutableList.Builder<ParticleTextureSheet> builder = ImmutableList.builder();
        builder.addAll(PARTICLE_TEXTURE_SHEETS);
        builder.add(CherryParticleTextureSheets.SHADER_SHEET);

        PARTICLE_TEXTURE_SHEETS = builder.build();
    }
}
