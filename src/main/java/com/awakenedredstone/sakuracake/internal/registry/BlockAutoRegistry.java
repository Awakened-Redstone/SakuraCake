package com.awakenedredstone.sakuracake.internal.registry;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.lang.reflect.Field;

public interface BlockAutoRegistry extends AutoRegistry<Block> {
    @Override
    default Class<Block> fieldType() {
        return Block.class;
    }

    @Override
    default Registry<Block> registry() {
        return Registries.BLOCK;
    }

    @Override
    default void postProcessField(String namespace, Block value, String identifier, Field field) {
        if (field.isAnnotationPresent(NoBlockItem.class)) return;
        Registry.register(Registries.ITEM, Identifier.of(namespace, identifier), createBlockItem(value, identifier));
    }

    default BlockItem createBlockItem(Block block, String identifier) {
        return new BlockItem(block, new Item.Settings());
    }
}
