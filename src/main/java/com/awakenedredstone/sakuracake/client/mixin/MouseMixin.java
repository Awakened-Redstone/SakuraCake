package com.awakenedredstone.sakuracake.client.mixin;

import com.awakenedredstone.sakuracake.client.render.CauldronHudRenderer;
import com.awakenedredstone.sakuracake.util.CherryMath;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.Mouse;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Mouse.class)
public class MouseMixin {
    @WrapWithCondition(method = "onMouseScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;scrollInHotbar(D)V"))
    private boolean interceptMouseScroll(PlayerInventory instance, double scrollAmount) {
        if (CauldronHudRenderer.cauldron != null) {
            int usedSlots = CauldronHudRenderer.cauldron.getUsedSlotCount();

            if (usedSlots <= 0) {
                return true;
            } else {
                CauldronHudRenderer.moveSlot((int) scrollAmount);
            }

            return false;
        }

        return true;
    }
}
