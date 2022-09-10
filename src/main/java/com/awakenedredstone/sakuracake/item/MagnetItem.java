package com.awakenedredstone.sakuracake.item;

import com.awakenedredstone.sakuracake.util.VectorHelper;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MagnetItem extends Item implements Unrepairable, Unenchantable {
    protected static final HashMap<UUID, UUID> pulledItems = new HashMap<>();
    protected final byte reach;
    protected final Random random;

    public MagnetItem(Settings settings, byte reach) {
        super(settings);
        this.reach = reach;
        this.random = new Random();
    }

    @Override
    public int getEnchantability() {
        return 0;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return this.isEnabled(stack);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.empty());
        tooltip.add(Text.translatable("item.sakuracake.magnet.status", this.isEnabled(stack) ? "§8enabled" : "§8disabled"));
        if (!context.isAdvanced()) {
            tooltip.add(Text.translatable("item.durability", stack.getMaxDamage() - stack.getDamage(), stack.getMaxDamage()).formatted(Formatting.DARK_GRAY));
        }
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return false;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (!world.isClient) {
            this.setEnabled(stack, !this.isEnabled(stack), player);
            player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_BELL, SoundCategory.PLAYERS, 0.2f, this.isEnabled(stack) ? 1 : 0);
            player.sendMessage(Text.translatable(String.format("item.sakuracake%s.magnet.%s",
                    player.getUuidAsString().equals("612db989-1441-48e7-a2e8-eee3c5caf334") ? ".silvervale" : "",
                    this.isEnabled(stack) ? "activate" : "deactivate")), true);
        }
        return TypedActionResult.success(stack, true);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient && this.isEnabled(stack) && entity instanceof PlayerEntity player) {
            List<ItemEntity> entityItems = world.getNonSpectatingEntities(ItemEntity.class, entity.getBoundingBox().expand(this.reach));
            for (ItemEntity item : entityItems) {
                if (getClosestPlayerWithMagnet(item, this.reach) != player) continue;
                //noinspection ConstantConditions
                if (!item.isAlive() || (stack.hasNbt() && stack.getNbt().getBoolean("PreventRemoteMovement")) || item.getScoreboardTags().contains("PreventMagnetMovement"))
                    continue;
                if (!pulledItems.containsKey(item.getUuid())) {
                    pulledItems.put(item.getUuid(), player.getUuid());
                    stack.damage(0, player, holder -> holder.damage(new DamageSource("brokenSakuraItem"), new Random().nextInt(this.reach) + 4));
                }
                if (!pulledItems.get(item.getUuid()).equals(player.getUuid())) continue;
                item.resetPickupDelay();
                this.moveItemToPlayer(item, player, 0.15f, pulledItems.size() > 100);

                /*if (canPickup(item, player)) {
                    stack.damage(1, player, holder -> holder.damage(new DamageSource("brokenSakuraItem"), new Random().nextInt(this.reach) + 4));
                    item.onPlayerCollision(player);
                }*/
            }
        }
    }

    public boolean canPickup(ItemEntity item, PlayerEntity player) {
        return !item.cannotPickup() && !(player.getInventory().getOccupiedSlotWithRoomForStack(item.getStack()) == -1 && player.getInventory().getEmptySlot() == -1);
    }

    public void setEnabled(ItemStack stack, boolean enabled) {
        setEnabled(stack, enabled, null);
    }

    public void setEnabled(ItemStack stack, boolean enabled, @Nullable PlayerEntity player) {
        if (player != null) pulledItems.values().removeAll(Collections.singleton(player.getUuid()));
        NbtCompound tag = stack.getOrCreateNbt();
        tag.putBoolean("enabled", enabled);
        stack.setNbt(tag);
    }

    protected boolean isEnabled(ItemStack stack) {
        return stack.getOrCreateNbt().getBoolean("enabled");
    }

    @Nullable
    protected PlayerEntity getClosestPlayerWithMagnet(ItemEntity item, byte radius) {
        List<PlayerEntity> players = item.getEntityWorld().getNonSpectatingEntities(PlayerEntity.class, item.getBoundingBox().expand(radius));
        if (players.isEmpty()) {
            return null;
        }
        PlayerEntity closest = players.get(0);
        double distance = radius;
        for (PlayerEntity player : players) {
            double temp = player.distanceTo(item);
            if (!(temp < distance) || !this.hasEnabledMagnetInRange(player, radius)) continue;
            closest = player;
            distance = temp;
        }
        return closest;
    }

    protected boolean hasEnabledMagnetInRange(PlayerEntity player, byte radius) {
        PlayerInventory inventory = player.getInventory();
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack stack = inventory.getStack(i);
            if (stack.isEmpty() || !(stack.getItem() instanceof MagnetItem) || !((MagnetItem) stack.getItem()).isEnabled(stack) || !(reach >= radius))
                continue;
            return true;
        }
        return false;
    }

    protected void moveItemToPlayer(ItemEntity item, PlayerEntity player, float speed, boolean instant) {
        if (instant) {
            item.setPosition(player.getPos());
        } else {
            Vec3d target = player.getPos();
            Vec3d current = item.getPos();
            float distance = item.distanceTo(player);
            float finalSpeed = speed;
            if (distance <= 0.3) finalSpeed = distance * 0.2f * speed;
            else if (distance < 1) finalSpeed = distance * speed;
            Vec3d velocity = VectorHelper.getMovementVelocity(current, target, finalSpeed);
            Vec3d playerVelocity = player.getVelocity();
            if (distance < 1.3) item.addVelocity(playerVelocity.x, 0, playerVelocity.z);
            item.addVelocity(velocity.x, velocity.y, velocity.z);
        }
    }

    public static void setEntityMotionFromVector(Entity entity, Vec3d originalPosVector, float modifier) {
        Vec3d entityVector = fromEntityCenter(entity);
        Vec3d finalVector = originalPosVector.subtract(entityVector);

        if (finalVector.length() > 1) {
            finalVector = finalVector.normalize();
        }

        entity.setVelocity(finalVector.multiply(modifier));
    }

    public static Vec3d fromEntityCenter(Entity e) {
        return new Vec3d(e.getX(), e.getY() - e.getHeightOffset() + e.getBoundingBox().getYLength() / 2, e.getZ());
    }
}
