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
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class MagnetItem extends Item implements Unrepairable, Unenchantable {
    protected static final HashMap<UUID, UUID> pulledItems = new HashMap<>();
    protected final byte reach;
    protected final Random random;

    public MagnetItem(Settings settings, byte reach) {
        super(settings);
        this.reach = reach;
        this. random = new Random();
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
        tooltip.add(LiteralText.EMPTY);
        tooltip.add(new TranslatableText("item.sakuracake.magnet.status", this.isEnabled(stack) ? "§8enabled" : "§8disabled"));
        if (!context.isAdvanced()) {
            tooltip.add(new TranslatableText("item.durability", stack.getMaxDamage() - stack.getDamage(), stack.getMaxDamage()).formatted(Formatting.DARK_GRAY));
        }
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return false;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient) {
            this.setEnabled(stack, !this.isEnabled(stack));
            user.sendMessage(new TranslatableText(String.format("item.sakuracake%s.magnet.%s",
                    user.getUuidAsString().equals("612db989-1441-48e7-a2e8-eee3c5caf334") ? ".silvervale" : "",
                    this.isEnabled(stack) ? "activate" : "deactivate")), true);
        }
        return TypedActionResult.success(stack, true);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient && this.isEnabled(stack) && entity instanceof PlayerEntity player) {
            List<ItemEntity> entityItems = world.getNonSpectatingEntities(ItemEntity.class, entity.getBoundingBox().expand(this.reach));
            for (ItemEntity item : entityItems) {
                if (canPickup(item, player)) {
                    stack.damage(1, player, holder -> holder.damage(new DamageSource("brokenSakuraItem"), new Random().nextInt(this.reach) + 4));
                    item.onPlayerCollision(player);
                }
            }
        }
    }

    public boolean canPickup(ItemEntity item, PlayerEntity player) {
        return !item.cannotPickup() && !(player.getInventory().getOccupiedSlotWithRoomForStack(item.getStack()) == -1 && player.getInventory().getEmptySlot() == -1);
    }

    private void setEnabled(ItemStack stack, boolean enabled) {
        NbtCompound tag = stack.getOrCreateNbt();
        tag.putBoolean("enabled", enabled);
        stack.setNbt(tag);
    }

    protected boolean isEnabled(ItemStack stack) {
        return stack.getOrCreateNbt().getBoolean("enabled");
    }

    protected void moveItemToPlayer(ItemEntity item, PlayerEntity player, float speed, boolean instant) {
        if (instant) {
            item.setPosition(player.getX(), player.getY(), player.getZ());
        } else {
            Vec3d target = VectorHelper.getVectorFromPos(player.getBlockPos());
            Vec3d current = VectorHelper.getVectorFromPos(item.getBlockPos());
            Vec3d velocity = VectorHelper.getMovementVelocity(current, target, speed);
            item.addVelocity(velocity.x, velocity.y, velocity.z);
            item.velocityModified = true;
        }
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
            if (stack.isEmpty() || !(stack.getItem() instanceof MagnetItem) || !((MagnetItem) stack.getItem()).isEnabled(stack) || !(reach >= radius)) continue;
            return true;
        }
        return false;
    }
}
