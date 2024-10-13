package com.awakenedredstone.sakuracake.internal.registry;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public interface ItemAutoRegistry extends AutoRegistry<Item> {
    @Override
    default Class<Item> fieldType() {
        return Item.class;
    }

    @Override
    default Registry<Item> registry() {
        return Registries.ITEM;
    }
}
