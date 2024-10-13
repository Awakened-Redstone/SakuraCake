package com.awakenedredstone.sakuracake.client;

import com.awakenedredstone.sakuracake.SakuraCake;
import com.awakenedredstone.sakuracake.client.event.RenderLayerRegistrationCallback;
import com.awakenedredstone.sakuracake.client.particle.ShaderParticle;
import com.awakenedredstone.sakuracake.client.render.Shaders;
import com.awakenedredstone.sakuracake.registry.CherryBlocks;
import com.awakenedredstone.sakuracake.registry.CherryParticles;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.minecraft.client.render.RenderLayer;

public final class SakuraCakeClient {
    public static void init() {
        SakuraCake.LOGGER.info("{} Client Initialized", SakuraCake.MOD_ID);
        ParticleFactoryRegistry.getInstance().register(CherryParticles.SPARKLE_PARTICLE, ShaderParticle.Factory::new);
        CoreShaderRegistrationCallback.EVENT.register(Shaders::register);
        RenderLayerRegistrationCallback.EVENT.register(blocks -> {
            blocks.put(CherryBlocks.CAULDRON, RenderLayer.getTranslucent());
        });
    }
}
