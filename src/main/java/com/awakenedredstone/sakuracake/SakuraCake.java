package com.awakenedredstone.sakuracake;

import com.awakenedredstone.sakuracake.internal.registry.AutoRegistry;
import com.awakenedredstone.sakuracake.registry.CherryBlockEntities;
import com.awakenedredstone.sakuracake.registry.CherryBlocks;
import com.awakenedredstone.sakuracake.registry.CherryItems;
/*? if fabric {*/
import com.awakenedredstone.sakuracake.registry.CherryParticles;
import dev.felnull.specialmodelloader.api.event.SpecialModelLoaderEvents;
//?}
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SakuraCake {
    public static final String MOD_ID = "sakuracake";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void init() {
        /*? if fabric {*/
        initRegistries();
        SpecialModelLoaderEvents.LOAD_SCOPE.register(() -> (resourceManager, location) -> MOD_ID.equals(location.getNamespace()));
        /*?}*/
    }

    public static void initRegistries() {
        AutoRegistry.init(CherryBlocks.class);
        AutoRegistry.init(CherryBlockEntities.class);
        AutoRegistry.init(CherryItems.class);
        AutoRegistry.init(CherryParticles.class);
    }

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    public static TagKey<Item> itemTag(String tag) {
        return TagKey.of(RegistryKeys.ITEM, id(tag));
    }
}
