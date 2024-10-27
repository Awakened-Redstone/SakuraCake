package com.awakenedredstone.sakuracake.mixin;

import com.awakenedredstone.sakuracake.nbt.NbtNull;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.StringNbtReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StringNbtReader.class)
public class StringNbtReaderMixin {
    @Inject(method = "parsePrimitive", at = @At("TAIL"), cancellable = true)
    private void parseNull(String input, CallbackInfoReturnable<NbtElement> cir) {
        if ("null".equalsIgnoreCase(input)) cir.setReturnValue(NbtNull.INSTANCE);
    }
}
