package com.awakenedredstone.sakuracake;

import com.awakenedredstone.sakuracake.entity.SitEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;

public class SakuraCake implements ModInitializer {
    private final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "sakuracake";
    public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.create(new Identifier(MOD_ID, "items")).icon(() -> new ItemStack(SakuraItems.SAKURA_MAGNET)).build();
    public static final EntityType<SitEntity> SIT_ENTITY_TYPE = Registry.register(Registry.ENTITY_TYPE, new Identifier("sit", "entity_sit"), FabricEntityTypeBuilder.<SitEntity>create(SpawnGroup.MISC, SitEntity::new).dimensions(EntityDimensions.fixed(0.001F, 0.001F)).build());

    @Override
    public void onInitialize() {
        SakuraItems.registerItems();
        new Color(0xE0E0E0);
    }
}
