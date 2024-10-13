package com.awakenedredstone.sakuracake.mixin;

import net.minecraft.registry.Registries;
import org.spongepowered.asm.mixin.Mixin;
//? if neoforge {
/*import com.awakenedredstone.sakuracake.SakuraCake;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
*/
//?}

@Mixin(Registries.class)
public abstract class RegistriesMixin {
    //? if neoforge {
    /*@Inject(method = "freezeRegistries", at = @At("HEAD"))
    private static <T> void addAllStuffToRegistry(CallbackInfo ci) {
        SakuraCake.initRegistries();
    }
    *///?}
}
