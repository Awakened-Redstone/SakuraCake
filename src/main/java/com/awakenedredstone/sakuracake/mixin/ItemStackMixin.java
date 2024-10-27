package com.awakenedredstone.sakuracake.mixin;

import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ItemStack stack)) return false;

        return ItemStack.areEqual((ItemStack) (Object) this, stack);
    }
}
