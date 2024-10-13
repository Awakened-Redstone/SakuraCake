package com.awakenedredstone.sakuracake.client.mixin;

import com.awakenedredstone.sakuracake.internal.mixin.RequireDevEnv;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gl.GlDebug;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RequireDevEnv
@Mixin(GlDebug.class)
public abstract class GlDebugMixin {
    @Unique private static final Map<Integer, Long> COOLDOWN = new HashMap<>();

    @Inject(at = @At(value = "HEAD"), method = "info(IIIIIJJ)V", cancellable = true)
    private static void suppressMessage(int source, int type, int id, int severity, int messageLength, long message, long l, CallbackInfo ci) {
        int hash = Objects.hash(source, type, id, severity, messageLength, message, l);
        if (System.currentTimeMillis() - COOLDOWN.getOrDefault(hash, 0L) < 10000) {
            ci.cancel();
        } else {
            COOLDOWN.put(hash, System.currentTimeMillis());
        }
    }
}
