package com.awakenedredstone.sakuracake.internal.registry;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public interface BlockEntityAutoRegistry extends AutoRegistry<BlockEntityType<?>> {

    @Override
    default Registry<BlockEntityType<?>> registry() {
        return Registries.BLOCK_ENTITY_TYPE;
    }

    @Override
    default Class<BlockEntityType<?>> fieldType() {
        return AutoRegistry.conform(BlockEntityType.class);
    }
}
