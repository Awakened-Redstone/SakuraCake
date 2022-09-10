package com.awakenedredstone.sakuracake.client;

import com.awakenedredstone.sakuracake.SakuraBlockEntities;
import com.awakenedredstone.sakuracake.SakuraCake;
import com.awakenedredstone.sakuracake.client.render.PedestalItemRender;
import com.awakenedredstone.sakuracake.entity.SitEntity;
import com.awakenedredstone.sakuracake.item.PhoenixElytraItem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class SakuraCakeClient implements ClientModInitializer {
    private static final KeyBinding elytraBoost = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.sakuracake.wingsBoost", // The translation key of the keybinding's name
            InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
            GLFW.GLFW_KEY_R, // The keycode of the key
            "keybind.category.sakuracake" // The translation key of the keybinding's category.
    ));

    private static final KeyBinding yeetItem = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.sakuracake.yeetItem", // The translation key of the keybinding's name
            InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
            GLFW.GLFW_KEY_LEFT_ALT, // The keycode of the key
            "keybind.category.sakuracake" // The translation key of the keybinding's category.
    ));

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(SakuraCake.SIT_ENTITY_TYPE, EmptyRenderer::new);
        EntityRendererRegistry.register(SakuraCake.YEETED_ITEM, ItemEntityRenderer::new);
        BlockEntityRendererRegistry.register(SakuraBlockEntities.SAKURA_PEDESTAL_BLOCK_ENTITY, PedestalItemRender::new);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player == null) return;

            ItemStack equippedStack = player.getEquippedStack(EquipmentSlot.CHEST);
            ItemCooldownManager cooldown = player.getItemCooldownManager();

            while (elytraBoost.wasPressed()) {
                if (!player.isFallFlying()) continue;
                if (!(equippedStack.getItem() instanceof PhoenixElytraItem)) continue;
                if (cooldown.isCoolingDown(equippedStack.getItem())) continue;

                Vec3d rotation = player.getRotationVector();
                Vec3d velocity = player.getVelocity();
                player.setVelocity(velocity.add(
                        rotation.x * 0.1 + (rotation.x * 30 - velocity.x) * 0.5,
                        rotation.y * 0.1 + (rotation.y * 30 - velocity.y) * 0.5,
                        rotation.z * 0.1 + (rotation.z * 30 - velocity.z) * 0.5));
                if (!equippedStack.getOrCreateNbt().getBoolean("noCooldown"))
                    cooldown.set(equippedStack.getItem(), 400);
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player == null) return;

            ItemStack handItem = player.getEquippedStack(EquipmentSlot.MAINHAND).copy();

            while (yeetItem.wasPressed()) {
                if (handItem.isEmpty()) continue;
                if (!Screen.hasControlDown()) handItem.setCount(1);
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeItemStack(handItem);
                ClientPlayNetworking.send(new Identifier(SakuraCake.MOD_ID, "yeet_item"), buf);
            }
        });
    }

    private static class EmptyRenderer extends EntityRenderer<SitEntity> {
        protected EmptyRenderer(EntityRendererFactory.Context ctx) {
            super(ctx);
        }

        @Override
        public boolean shouldRender(SitEntity entity, Frustum frustum, double d, double e, double f) {
            return false;
        }

        @Override
        public Identifier getTexture(SitEntity entity) {
            return null;
        }
    }
}
