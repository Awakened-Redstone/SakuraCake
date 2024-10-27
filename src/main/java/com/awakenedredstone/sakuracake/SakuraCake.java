package com.awakenedredstone.sakuracake;

import com.awakenedredstone.sakuracake.internal.registry.AutoRegistry;
import com.awakenedredstone.sakuracake.network.SelectedCauldronSlotPacket;
import com.awakenedredstone.sakuracake.registry.*;
/*? if fabric {*/
/*import com.mojang.serialization.Codec;
import dev.felnull.specialmodelloader.api.event.SpecialModelLoaderEvents;
*///?}
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
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
        /*initRegistries();
        *//*?}*/

        PayloadTypeRegistry.playC2S().register(SelectedCauldronSlotPacket.ID, SelectedCauldronSlotPacket.PACKET_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(SelectedCauldronSlotPacket.ID, SelectedCauldronSlotPacket::handlePacket);
    }

    public static void initRegistries() {
        AutoRegistry.init(CherryBlocks.class);
        AutoRegistry.init(CherryBlockEntities.class);
        AutoRegistry.init(CherryItems.class);
        AutoRegistry.init(CherryParticles.class);
        AutoRegistry.init(CherryRecipeTypes.class);
        AutoRegistry.init(CherryRecipeSerializers.class);
        AutoRegistry.init(CherryEntityTypes.class);
        AutoRegistry.init(CherryDataComponentTypes.class);
    }

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    public static Identifier id(String... path) {
        return Identifier.of(MOD_ID, String.join("/", path));
    }

    public static TagKey<Item> itemTag(String tag) {
        return TagKey.of(RegistryKeys.ITEM, id(tag));
    }

    public static boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
}
