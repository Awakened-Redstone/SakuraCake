package com.awakenedredstone.sakuracake.item.magnet;

import com.awakenedredstone.sakuracake.item.MagnetItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public class AncientMagnet extends MagnetItem {
    private final byte pullRange = (byte) (this.reach / (byte) 2);

    public AncientMagnet(Settings settings) {
        super(settings, (byte) 13);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient && this.isEnabled(stack) && entity instanceof PlayerEntity player) {
            List<ItemEntity> pickupItems = world.getNonSpectatingEntities(ItemEntity.class, entity.getBoundingBox().expand(this.reach));
            List<ItemEntity> moveItems = world.getNonSpectatingEntities(ItemEntity.class, entity.getBoundingBox().expand(this.pullRange));
            pickupItems.removeAll(moveItems);
            UUID uuid = player.getUuid();
            for (ItemEntity item : pickupItems) {
                if (!item.isAlive()) continue;
                if (canPickup(item, player)) {
                    stack.damage(1, player,
                            holder -> holder.damage(new DamageSource("brokenSakuraItem").setBypassesArmor().setUnblockable(),
                                    this.random.nextInt(this.reach) + damageFromPulledItems(holder.getUuid()) + 4));
                    item.onPlayerCollision(player);
                }
            }
            for (ItemEntity item : moveItems) {
                //noinspection ConstantConditions
                if (!item.isAlive() || (stack.hasNbt() && stack.getNbt().getBoolean("PreventRemoteMovement")) || item.getScoreboardTags().contains("PreventMagnetMovement"))
                    continue;
                if (!pulledItems.containsKey(item.getUuid())) {
                    pulledItems.put(item.getUuid(), uuid);
                    stack.damage(1, player,
                            holder -> holder.damage(new DamageSource("brokenSakuraItem"),
                                    this.random.nextInt(this.reach) + this.random.nextInt((int) pulledItems.values().stream().map(uuid::equals).count()) + 4));
                }
                if (!pulledItems.get(item.getUuid()).equals(player.getUuid())) continue;
                item.resetPickupDelay();
                this.moveItemToPlayer(item, player, 3, false);
            }
        }
    }

    private short damageFromPulledItems(UUID uuid) {
        float rand = random.nextFloat();
        short initialDamage = (short) Math.min(pulledItems.values().stream().map(uuid::equals).count(), Short.MAX_VALUE);
        return (short) Math.round((initialDamage) * Math.max((MathHelper.floorMod(rand, 0.4f) + (0.4f * Math.max(MathHelper.floor(rand / 0.4f), 1)) + MathHelper.floorMod(random.nextFloat(), 0.35f)), 1.2f));
    }

}
