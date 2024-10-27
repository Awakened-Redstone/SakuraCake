package com.awakenedredstone.sakuracake.mixin;

import com.awakenedredstone.sakuracake.nbt.NbtNull;
import net.minecraft.nbt.NbtType;
import net.minecraft.nbt.NbtTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NbtTypes.class)
public class NbtTypesMixin {
    @Inject(method = "byId", at = @At("HEAD"), cancellable = true)
    private static void addNull(int id, CallbackInfoReturnable<NbtType<?>> cir) {
        if (id == Byte.MAX_VALUE) cir.setReturnValue(NbtNull.TYPE);
    }
}
