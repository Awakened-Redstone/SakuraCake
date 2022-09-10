package com.awakenedredstone.sakuracake;

import com.awakenedredstone.sakuracake.block.SakuraPedestalBlock;
import io.wispforest.owo.registration.reflect.BlockRegistryContainer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static com.awakenedredstone.sakuracake.SakuraItems.IRON_MAGNET;

public class SakuraBlocks implements BlockRegistryContainer {
    public static final SakuraPedestalBlock SAKURA_PEDESTAL = new SakuraPedestalBlock(FabricBlockSettings.of(Material.STONE).hardness(4.0f));

    @Override
    public BlockItem createBlockItem(Block block, String identifier) {
        return new BlockItem(block, new Item.Settings().group(SakuraCake.ITEM_GROUP));
    }
}
