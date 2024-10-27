package com.awakenedredstone.sakuracake.registry;

import com.awakenedredstone.sakuracake.SakuraCake;
import com.awakenedredstone.sakuracake.internal.registry.AutoRegistry;
import com.awakenedredstone.sakuracake.internal.registry.ItemAutoRegistry;
import com.awakenedredstone.sakuracake.internal.registry.RegistryNamespace;
import com.awakenedredstone.sakuracake.registry.item.BlossomDustItem;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;

import java.util.List;

@RegistryNamespace(SakuraCake.MOD_ID)
public class CherryItems implements ItemAutoRegistry {
    public static final Item BLOSSOM_DUST = new BlossomDustItem(new Item.Settings());
    //public static final Item AMETHYTE_WINDOW = new Item(new Item.Settings());
    public static final Item WAND = new Item(new Item.Settings());
    public static final Item CHERRY_BLOSSOM_CAKE = new Item(new Item.Settings());

    public static final RegistryKey<ItemGroup> ITEM_GROUP_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), SakuraCake.id("item_group"));
    public static final ItemGroup ITEM_GROUP = FabricItemGroup.builder()
      .icon(() -> new ItemStack(CHERRY_BLOSSOM_CAKE))
      .displayName(Text.translatable("itemGroup.sakuracake"))
      .build();

    @Override
    public void afterFieldProcessing() {
        Registry.register(Registries.ITEM_GROUP, ITEM_GROUP_KEY, ITEM_GROUP);

        List<Item> items = Registries.ITEM.streamEntries()
          .filter(ref -> ref.getKeyOrValue().left().isPresent())
          .filter(ref -> ref.getKeyOrValue().left().get().getValue().getNamespace().equals(SakuraCake.MOD_ID))
          .map(RegistryEntry.Reference::value)
          .toList();

        ItemGroupEvents.modifyEntriesEvent(ITEM_GROUP_KEY).register(itemGroup -> items.forEach(itemGroup::add));
    }
}
