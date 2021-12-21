package com.awakenedredstone.sakuracake.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Random;

@Mixin(FireworkRocketEntity.class)
public abstract class FireworkRocketEntityMixin extends ProjectileEntity {

    public FireworkRocketEntityMixin(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "explode", at = @At(value = "HEAD"))
    private void explode(CallbackInfo ci) {
        List<WolfEntity> wolfs = this.world.getNonSpectatingEntities(WolfEntity.class, this.getBoundingBox().expand(12));
        for (WolfEntity wolf : wolfs) {
        }
    }
}
