package com.awakenedredstone.sakuracake.registry;

import com.awakenedredstone.sakuracake.SakuraCake;
import com.awakenedredstone.sakuracake.internal.registry.BlockAutoRegistry;
import com.awakenedredstone.sakuracake.internal.registry.RegistryNamespace;
import com.awakenedredstone.sakuracake.registry.block.CherryCauldronBlock;
import com.awakenedredstone.sakuracake.registry.block.FocusedAuraBlock;
import com.awakenedredstone.sakuracake.registry.block.PedestalBlock;
import com.awakenedredstone.sakuracake.registry.block.PillarBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;

@RegistryNamespace(SakuraCake.MOD_ID)
public class CherryBlocks implements BlockAutoRegistry {
    public static final CherryCauldronBlock CAULDRON = new CherryCauldronBlock(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).requiresTool().strength(2.0F).nonOpaque());
    //public static final PillarBlock PILLAR = new PillarBlock(AbstractBlock.Settings.create());
    public static final PedestalBlock PEDESTAL = new PedestalBlock(AbstractBlock.Settings.create().strength(2.4F).nonOpaque());
    public static final FocusedAuraBlock FOCUSED_AURA = new FocusedAuraBlock(AbstractBlock.Settings.create().noCollision().replaceable().hardness(0).resistance(1000000).nonOpaque());
}
