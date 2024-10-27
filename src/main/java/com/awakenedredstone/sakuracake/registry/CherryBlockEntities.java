package com.awakenedredstone.sakuracake.registry;

import com.awakenedredstone.sakuracake.SakuraCake;
import com.awakenedredstone.sakuracake.internal.registry.BlockEntityAutoRegistry;
import com.awakenedredstone.sakuracake.internal.registry.RegistryNamespace;
import com.awakenedredstone.sakuracake.registry.block.entity.CherryCauldronBlockEntity;
import com.awakenedredstone.sakuracake.registry.block.entity.FocusedAuraBlockEntity;
import com.awakenedredstone.sakuracake.registry.block.entity.PedestalBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityType;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;

@RegistryNamespace(SakuraCake.MOD_ID)
public class CherryBlockEntities implements BlockEntityAutoRegistry {
    public static final BlockEntityType<FocusedAuraBlockEntity> FOCUSED_AURA = create(FocusedAuraBlockEntity::new, CherryBlocks.FOCUSED_AURA);
    public static final BlockEntityType<CherryCauldronBlockEntity> CAULDRON = create(CherryCauldronBlockEntity::new, CherryBlocks.CAULDRON);
    public static final BlockEntityType<PedestalBlockEntity> PEDESTAL = create(PedestalBlockEntity::new, CherryBlocks.PEDESTAL);

    @SuppressWarnings({"DataFlowIssue", "unchecked"})
    public static <T extends BlockEntity> BlockEntityType<T> create(BlockEntityType.BlockEntityFactory<? extends T> factory, Block... blocks) {
        return ((FabricBlockEntityType.Builder<T>) (Object) BlockEntityType.Builder.create(factory, blocks)).build();
    }
}
