package com.awakenedredstone.sakuracake.item;

import net.fabricmc.fabric.api.entity.event.v1.FabricElytraItem;
import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PhoenixElytraItem extends Item implements FabricElytraItem, Wearable, Unrepairable, Unenchantable {
    public PhoenixElytraItem(Settings settings) {
        super(settings);
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSER_BEHAVIOR);
    }

    @Override
    public boolean damage(DamageSource source) {
        return source.isOutOfWorld();
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return false;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(itemStack);
        ItemStack itemStack2 = user.getEquippedStack(equipmentSlot);
        if (itemStack2.isEmpty()) {
            user.equipStack(equipmentSlot, itemStack.copy());
            if (!world.isClient()) {
                user.incrementStat(Stats.USED.getOrCreateStat(this));
            }
            itemStack.setCount(0);
            return TypedActionResult.success(itemStack, world.isClient());
        }
        return TypedActionResult.fail(itemStack);
    }

    @Override
    @Nullable
    public SoundEvent getEquipSound() {
        return SoundEvents.ITEM_ARMOR_EQUIP_ELYTRA;
    }
}
