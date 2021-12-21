package com.awakenedredstone.sakuracake.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class ItemPickupEvent {
    public static final Event<PickupEvent> EVENT = EventFactory.createArrayBacked(PickupEvent.class, callbacks -> (player, itemEntity, itemStack) -> {
        for (PickupEvent callback : callbacks) {
            callback.onItemPickup(player, itemEntity, itemStack);
        }
    });

    @FunctionalInterface
    public interface PickupEvent {
        void onItemPickup(PlayerEntity player, ItemEntity itemEntity, ItemStack itemStack);
    }
}
