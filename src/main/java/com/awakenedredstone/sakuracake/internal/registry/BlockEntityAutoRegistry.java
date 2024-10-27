package com.awakenedredstone.sakuracake.internal.registry;

import com.awakenedredstone.sakuracake.util.Cast;
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
        return Cast.conform(BlockEntityType.class);
    }
}
