package com.awakenedredstone.sakuracake.mixin;

import net.minecraft.entity.player.HungerManager;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(HungerManager.class)
public class HungerManagerMixin {

    @Shadow private int foodLevel;
    @Shadow private float saturationLevel;

    @Inject(method = "add", at = @At(value = "HEAD"))
    private void add(int food, float saturationModifier, CallbackInfo ci) {
        this.foodLevel = Math.max(this.foodLevel, 0);
        this.saturationLevel = Math.max(this.saturationLevel, 0);
    }

    @Inject(method = "eat", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/HungerManager;add(IF)V"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void eat(Item item, ItemStack stack, CallbackInfo ci, FoodComponent foodComponent) {
        int food = foodComponent.getHunger();
        float saturationModifier = foodComponent.getSaturationModifier();
        if (food > 1E+4) {
            this.foodLevel = (int)Math.min(((long)food + (long)this.foodLevel), 2147483647);
            this.saturationLevel = Math.min(this.saturationLevel + food * saturationModifier * 2.0f, this.foodLevel * (saturationModifier / 2));
            ci.cancel();
        }
    }
}
