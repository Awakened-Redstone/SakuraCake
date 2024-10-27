package com.awakenedredstone.sakuracake.duck;

import net.minecraft.entity.ItemEntity;

public interface CauldronDrop {
    boolean sakuraCake$isCauldronDrop();
    void sakuraCake$setCauldronDrop(boolean isDrop);

    static CauldronDrop cast(ItemEntity entity) {
        return (CauldronDrop) entity;
    }
}
