package com.awakenedredstone.sakuracake.mixin;

import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.WitherEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;

@Mixin(WitherEntity.class)
public class WitherEntityMixin {

    @Inject(method = "createWitherAttributes", at = @At(value = "RETURN"), cancellable = true)
    private static void createWitherAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir) {
        cir.setReturnValue(cir.getReturnValue().add(EntityAttributes.GENERIC_MAX_HEALTH, 600.0));
    }
}
