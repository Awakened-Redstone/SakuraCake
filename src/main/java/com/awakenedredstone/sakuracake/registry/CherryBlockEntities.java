package com.awakenedredstone.sakuracake.registry;

import com.awakenedredstone.sakuracake.SakuraCake;
import com.awakenedredstone.sakuracake.internal.registry.BlockEntityAutoRegistry;
import com.awakenedredstone.sakuracake.internal.registry.RegistryNamespace;
import com.awakenedredstone.sakuracake.registry.block.entity.FocusedAuraBlockEntity;
import net.minecraft.block.entity.BlockEntityType;

@RegistryNamespace(SakuraCake.MOD_ID)
public class CherryBlockEntities implements BlockEntityAutoRegistry {
    public static final BlockEntityType<FocusedAuraBlockEntity> FOCUSED_AURA = BlockEntityType.Builder.create(FocusedAuraBlockEntity::new, CherryBlocks.FOCUSED_AURA).build();
}
