package com.awakenedredstone.sakuracake.client;

import com.awakenedredstone.sakuracake.SakuraCake;
import com.awakenedredstone.sakuracake.client.particle.AbsorbParticle;
import com.awakenedredstone.sakuracake.client.particle.CauldronBubbleParticle;
import com.awakenedredstone.sakuracake.client.particle.CauldronBubblePopParticle;
import com.awakenedredstone.sakuracake.client.particle.ShaderParticle;
import com.awakenedredstone.sakuracake.client.render.CherryHudRenderer;
import com.awakenedredstone.sakuracake.client.render.CherryShaders;
import com.awakenedredstone.sakuracake.client.render.block.PedestalRenderer;
import com.awakenedredstone.sakuracake.event.WorldEvents;
import com.awakenedredstone.sakuracake.registry.CherryBlockEntities;
import com.awakenedredstone.sakuracake.registry.CherryParticles;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

/*? if neoforge {*/
/*import com.awakenedredstone.sakuracake.client.event.RenderLayerRegistrationCallback;
import com.awakenedredstone.sakuracake.registry.CherryBlocks;
import net.minecraft.client.render.RenderLayer;
*//*?}*/

/*? if fabric {*/
import dev.felnull.specialmodelloader.api.event.SpecialModelLoaderEvents;
/*?}*/

public final class SakuraCakeClient {
    private static long ticks = 0;

    public static void init() {
        BlockEntityRendererFactories.register(CherryBlockEntities.PEDESTAL, PedestalRenderer::new);

        CoreShaderRegistrationCallback.EVENT.register(CherryShaders::register);
        ParticleFactoryRegistry.getInstance().register(CherryParticles.CAULDRON_BUBBLE, CauldronBubbleParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(CherryParticles.CAULDRON_BUBBLE_POP, CauldronBubblePopParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(CherryParticles.FOCUSED_AURA, new ShaderParticle.Factory());
        ParticleFactoryRegistry.getInstance().register(CherryParticles.ABSORB, AbsorbParticle.Factory::new);
        /*? if neoforge {*/
        /*RenderLayerRegistrationCallback.EVENT.register(blocks -> {
            blocks.put(CherryBlocks.CAULDRON, SakuraCake.isModLoaded("sodium") ? RenderLayer.getTranslucent() : RenderLayer.CUTOUT_MIPPED);
        });
        *//*?}*/

        ClientTickEvents.END_CLIENT_TICK.register(client -> ticks++);

        HudRenderCallback.EVENT.register(CherryHudRenderer::render);

        /*? if fabric {*/
        SpecialModelLoaderEvents.LOAD_SCOPE.register(() -> (resourceManager, location) -> SakuraCake.MOD_ID.equals(location.getNamespace()));
        /*?}*/

        WorldEvents.registerEvent(SakuraCake.id("absorb_item"), (world, pos, data, random) -> {
            for (int s = 0; s < 10; ++s) {
                double velocityX = random.nextGaussian() * 0.02;
                double velocityY = random.nextGaussian() * 0.02;
                double velocityZ = random.nextGaussian() * 0.02;

                double offsetX = random.nextGaussian() * 0.1 - 0.05 + 0.5;
                double offsetY = random.nextGaussian() * 0.1 - 0.05 + 0.5;
                double offsetZ = random.nextGaussian() * 0.1 - 0.05 + 0.5;
                world.addParticle(ParticleTypes.WHITE_SMOKE, pos.getX() + offsetX, pos.getY() + offsetY, pos.getZ() + offsetZ, velocityX, velocityY, velocityZ);
            }
            world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_DECORATED_POT_INSERT, SoundCategory.BLOCKS, 0.5f, 1f, true);
        });
        WorldEvents.registerEvent(SakuraCake.id("empty_cauldron"), (world, pos, data, random) -> {
            world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_DECORATED_POT_INSERT, SoundCategory.BLOCKS, 0.5f, 0f, true);
        });
        WorldEvents.registerEvent(SakuraCake.id("empty_pedestal"), (world, pos, data, random) -> {
            world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_DECORATED_POT_INSERT, SoundCategory.BLOCKS, 0.5f, 0f, true);
        });
        WorldEvents.registerEvent(SakuraCake.id("insert_into_pedestal"), (world, pos, data, random) -> {
            world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_DECORATED_POT_INSERT, SoundCategory.BLOCKS, 0.5f, 1f, true);
        });

        ModelLoadingPlugin.register(context -> {
            context.addModels(PedestalRenderer.CUBE_MODEL);
        });
    }

    public static long getTicks() {
        return ticks;
    }
}
