package com.awakenedredstone.sakuracake.mixin;

import com.awakenedredstone.sakuracake.duck.CauldronDrop;
import net.minecraft.entity.ItemEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public class ItemEntityMixin implements CauldronDrop {
    @Unique boolean cauldronDrop = false;

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readExtraData(NbtCompound nbt, CallbackInfo ci) {
        cauldronDrop = nbt.getBoolean("SakuraCake_CauldronDrop");
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeExtraData(NbtCompound nbt, CallbackInfo ci) {
        if (cauldronDrop) {
            nbt.putBoolean("SakuraCake_CauldronDrop", true);
        }
    }

    @Override
    public boolean sakuraCake$isCauldronDrop() {
        return cauldronDrop;
    }

    @Override
    public void sakuraCake$setCauldronDrop(boolean isDrop) {
        cauldronDrop = isDrop;
    }
}
