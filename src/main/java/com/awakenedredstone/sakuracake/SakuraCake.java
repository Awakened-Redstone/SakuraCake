package com.awakenedredstone.sakuracake;

import com.awakenedredstone.sakuracake.entity.ItemProjectileEntity;
import com.awakenedredstone.sakuracake.entity.SitEntity;
import io.wispforest.owo.registration.reflect.FieldRegistrationHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class SakuraCake implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "sakuracake";
    public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.create(new Identifier(MOD_ID, "items")).icon(() -> new ItemStack(SakuraItems.SAKURA_MAGNET)).build();
    public static final EntityType<SitEntity> SIT_ENTITY_TYPE = Registry.register(Registry.ENTITY_TYPE, new Identifier(MOD_ID, "entity_sit"), FabricEntityTypeBuilder.<SitEntity>create(SpawnGroup.MISC, SitEntity::new).dimensions(EntityDimensions.fixed(0.0f, 0.0f)).build());
    public static final EntityType<ItemProjectileEntity> YEETED_ITEM = Registry.register(Registry.ENTITY_TYPE, new Identifier(MOD_ID, "yeeted_item"), FabricEntityTypeBuilder.<ItemProjectileEntity>create(SpawnGroup.MISC, ItemProjectileEntity::new).dimensions(EntityDimensions.fixed(0.25f, 0.25f)).build());

    private static final Map<ServerPlayerEntity, Short> yeetDelay = new HashMap<>();

    @Override
    public void onInitialize() {
        SakuraSounds.registerSounds();
        FieldRegistrationHandler.register(SakuraItems.class, MOD_ID, false);
        FieldRegistrationHandler.register(SakuraBlocks.class, MOD_ID, false);
        FieldRegistrationHandler.register(SakuraBlockEntities.class, MOD_ID, false);

        ServerPlayNetworking.registerGlobalReceiver(new Identifier(SakuraCake.MOD_ID, "yeet_item"), (server, player, handler, buf, responseSender) -> {
            ItemStack itemStack = buf.readItemStack();
            server.execute(() -> {
                if (player.isSpectator()) return;
                if (yeetDelay.get(player) != null)
                    if (yeetDelay.get(player) > 0) return;
                yeetDelay.put(player, (short) 5);
                Vec3d pos = player.getEyePos();
                ItemProjectileEntity itemEntity = new ItemProjectileEntity(player.world, pos.x, pos.y, pos.z, itemStack, 0, 0, 0);
                itemEntity.setVelocity(player, player.getPitch(), player.getYaw(), 0.0f, 3f, 1.0f);
                if (!player.isCreative()) player.getInventory().getMainHandStack().decrement(itemStack.getCount());
                player.world.spawnEntity(itemEntity);
            });
        });

        ServerTickEvents.END_SERVER_TICK.register((server) -> yeetDelay.forEach((player, delay) -> {
            if (delay > 0) yeetDelay.put(player, --delay);
        }));
    }
}
