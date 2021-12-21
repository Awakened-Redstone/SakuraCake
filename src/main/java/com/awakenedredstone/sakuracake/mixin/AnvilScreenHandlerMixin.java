package com.awakenedredstone.sakuracake.mixin;

import com.awakenedredstone.sakuracake.item.Unenchantable;
import com.awakenedredstone.sakuracake.item.Unrepairable;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Items;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {

    public AnvilScreenHandlerMixin(@Nullable ScreenHandlerType<?> screenHandlerType, int i, PlayerInventory playerInventory, ScreenHandlerContext screenHandlerContext) {
        super(screenHandlerType, i, playerInventory, screenHandlerContext);
    }

    @Inject(method = "updateResult",
            cancellable = true,
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/item/EnchantedBookItem;getEnchantmentNbt(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/nbt/NbtList;",
                    shift = At.Shift.AFTER),
            slice = @Slice(
                    from = @At(value = "FIELD", target = "Lnet/minecraft/screen/AnvilScreenHandler;repairItemUsage:I"),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;canRepair(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z")))
    private void updateResult(CallbackInfo ci) {
        boolean isBook = this.input.getStack(1).isOf(Items.ENCHANTED_BOOK) && !EnchantedBookItem.getEnchantmentNbt(this.input.getStack(1)).isEmpty();
        if (this.input.getStack(0).getItem() instanceof Unrepairable && !isBook) {
            ci.cancel();
        }
        if (this.input.getStack(0).getItem() instanceof Unenchantable && (isBook || this.input.getStack(1).hasEnchantments())) {
            ci.cancel();
        }
    }
}
