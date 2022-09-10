package com.awakenedredstone.sakuracake.entity;

import com.awakenedredstone.sakuracake.entity.damage.ItemProjectileDamageSource;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Collection;

public class ItemProjectileEntity extends ItemEntity {
    public ItemProjectileEntity(EntityType<? extends ItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public ItemProjectileEntity(World world, double x, double y, double z, ItemStack stack) {
        super(world, x, y, z, stack);
    }

    public ItemProjectileEntity(World world, double x, double y, double z, ItemStack stack, double velocityX, double velocityY, double velocityZ) {
        super(world, x, y, z, stack, velocityX, velocityY, velocityZ);
        super.setPickupDelay(2);
    }

    public void setVelocity(Entity shooter, float pitch, float yaw, float roll, float speed, float divergence) {
        float f = -MathHelper.sin(yaw * ((float)Math.PI / 180)) * MathHelper.cos(pitch * ((float)Math.PI / 180));
        float g = -MathHelper.sin((pitch + roll) * ((float)Math.PI / 180));
        float h = MathHelper.cos(yaw * ((float)Math.PI / 180)) * MathHelper.cos(pitch * ((float)Math.PI / 180));
        this.setVelocity(f, g, h, speed, divergence);
        Vec3d vec3d = shooter.getVelocity();
        this.setVelocity(this.getVelocity().add(vec3d.x, shooter.isOnGround() ? 0.0 : vec3d.y, vec3d.z));
    }

    public void setVelocity(double x, double y, double z, float speed, float divergence) {
        Vec3d vec3d = new Vec3d(x, y, z).normalize().add(this.random.nextGaussian() * (double)0.0075f * (double)divergence, this.random.nextGaussian() * (double)0.0075f * (double)divergence, this.random.nextGaussian() * (double)0.0075f * (double)divergence).multiply(speed);
        this.setVelocity(vec3d);
        double d = vec3d.horizontalLength();
        //noinspection SuspiciousNameCombination
        this.setYaw((float)(MathHelper.atan2(vec3d.x, vec3d.z) * 57.2957763671875));
        this.setPitch((float)(MathHelper.atan2(vec3d.y, d) * 57.2957763671875));
        this.prevYaw = this.getYaw();
        this.prevPitch = this.getPitch();
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        if (cannotPickup()) return;
        float damage = MathHelper.ceil(MathHelper.clamp(this.getVelocity().length() * 2, 0.0, Integer.MAX_VALUE));
        if (damage > 1) {
            float additionalDamage = ((float)this.getStack().getCount() / 10);
            Collection<EntityAttributeModifier> entityAttributeModifiers = this.getStack().getAttributeModifiers(EquipmentSlot.MAINHAND).get(EntityAttributes.GENERIC_ATTACK_DAMAGE);
            if (!entityAttributeModifiers.isEmpty()) {
                additionalDamage += entityAttributeModifiers.stream().toList().get(0).getValue();
            }
            additionalDamage += EnchantmentHelper.getAttackDamage(this.getStack(), EntityGroup.DEFAULT);
            player.damage(new ItemProjectileDamageSource("itemYeet", this, player).setProjectile(), damage + additionalDamage);
            this.setVelocity(this.getVelocity().multiply(-0.1));
            this.setYaw(this.getYaw() + 180.0f);
            this.prevYaw += 180.0f;
            return;
        }
        super.onPlayerCollision(player);
    }
}
