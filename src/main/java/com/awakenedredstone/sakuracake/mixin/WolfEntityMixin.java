package com.awakenedredstone.sakuracake.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WolfEntity.class)
public abstract class WolfEntityMixin extends TameableEntity {

    protected WolfEntityMixin(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "onDeath", at = @At(value = "TAIL"))
    private void onDeath(DamageSource source, CallbackInfo ci) {
        if (this.world instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.SOUL, this.getX(), this.getY(), this.getZ(), 12, 0.3, 0.3, 0.3, 1);
        }
     }
}
