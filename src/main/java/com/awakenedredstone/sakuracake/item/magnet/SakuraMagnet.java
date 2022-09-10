package com.awakenedredstone.sakuracake.item.magnet;

import com.awakenedredstone.sakuracake.events.ItemPickupEvent;
import com.awakenedredstone.sakuracake.item.MagnetItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class SakuraMagnet extends MagnetItem {
    private final byte pullRange = (byte) (this.reach / (byte) 2);

    public SakuraMagnet(Settings settings) {
        super(settings, (byte) 13);

        ItemPickupEvent.EVENT.register((player, itemEntity, itemStack) -> pulledItems.remove(itemEntity.getUuid()));
    }

    @Override
    public boolean damage(DamageSource source) {
        return source.isOutOfWorld();
    }

    @Override
    public boolean canPickup(ItemEntity item, PlayerEntity player) {
        return !(player.getInventory().getOccupiedSlotWithRoomForStack(item.getStack()) == -1 && player.getInventory().getEmptySlot() == -1);
    }

    @Override //TODO: Better smoother way to move items without performance impact
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient && this.isEnabled(stack) && entity instanceof PlayerEntity player) {
            List<ItemEntity> pickupItems = world.getNonSpectatingEntities(ItemEntity.class, entity.getBoundingBox().expand(this.reach));
            List<ItemEntity> moveItems = world.getNonSpectatingEntities(ItemEntity.class, entity.getBoundingBox().expand(this.pullRange));
            pickupItems.removeAll(moveItems);
            for (ItemEntity item : pickupItems) {
                if (!item.isAlive()) continue;
                if (canPickup(item, player)) {
                    item.resetPickupDelay();
                    item.onPlayerCollision(player);
                }
            }
            for (ItemEntity item : moveItems) {
                if (getClosestPlayerWithMagnet(item, pullRange) != player) continue;
                //noinspection ConstantConditions
                if (!item.isAlive() || (stack.hasNbt() && stack.getNbt().getBoolean("PreventRemoteMovement")) || item.getScoreboardTags().contains("PreventMagnetMovement"))
                    continue;
                if (!pulledItems.containsKey(item.getUuid())) {
                    pulledItems.put(item.getUuid(), player.getUuid());
                    stack.damage(0, player, holder -> holder.damage(new DamageSource("impossibleAction"), Float.MAX_VALUE));
                }
                if (!pulledItems.get(item.getUuid()).equals(player.getUuid())) continue;
                item.resetPickupDelay();
                this.moveItemToPlayer(item, player, 0.15f, pulledItems.size() > 100);
            }
        }
    }
}
