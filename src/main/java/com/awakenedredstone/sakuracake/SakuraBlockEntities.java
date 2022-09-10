package com.awakenedredstone.sakuracake;

import com.awakenedredstone.sakuracake.block.container.SakuraPedestalBlockEntity;
import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

public class SakuraBlockEntities implements AutoRegistryContainer<BlockEntityType<?>> {

    public static final BlockEntityType<SakuraPedestalBlockEntity> SAKURA_PEDESTAL_BLOCK_ENTITY = FabricBlockEntityTypeBuilder.create(SakuraPedestalBlockEntity::new, SakuraBlocks.SAKURA_PEDESTAL).build();

    @Override
    public Registry<BlockEntityType<?>> getRegistry() {
        return Registry.BLOCK_ENTITY_TYPE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<BlockEntityType<?>> getTargetFieldType() {
        return (Class<BlockEntityType<?>>) (Object) BlockEntityType.class;
    }
}
