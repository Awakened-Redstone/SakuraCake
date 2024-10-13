package com.awakenedredstone.sakuracake.client.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;

import java.util.Map;

public interface RenderLayerRegistrationCallback {
    Event<RegistrationContext> EVENT = EventFactory.createArrayBacked(RegistrationContext.class, callbacks -> context -> {
        for (RegistrationContext callback : callbacks) {
            callback.register(context);
        }
    });

    @FunctionalInterface
    interface RegistrationContext {
        void register(Map<Block, RenderLayer> blocks);
    }
}
