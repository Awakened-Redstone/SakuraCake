package com.awakenedredstone.sakuracake.client.render;

import com.awakenedredstone.sakuracake.SakuraCake;
import com.awakenedredstone.sakuracake.client.mixin.acessor.DrawContextAccessor;
import com.awakenedredstone.sakuracake.network.SelectedCauldronSlotPacket;
import com.awakenedredstone.sakuracake.registry.CherryBlocks;
import com.awakenedredstone.sakuracake.registry.CherryItems;
import com.awakenedredstone.sakuracake.registry.block.entity.CherryCauldronBlockEntity;
import com.awakenedredstone.sakuracake.util.CherryMath;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.Scaling;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

public class CauldronHudRenderer {
    public static CherryCauldronBlockEntity cauldron;
    public static int selectedSlot = 0;
    private static int referenceSlot = 0;
    private static double currentOffset = 0;
    private static int toOffset = 0;

    private static void selectSlot(int slot) {
        if (cauldron == null) return;

        final int usedSlots = CauldronHudRenderer.cauldron.getUsedSlotCount();
        selectedSlot = usedSlots <= 1 ? 0 : CherryMath.wrap(slot, 0, usedSlots - 1);

        ClientPlayNetworking.send(new SelectedCauldronSlotPacket(selectedSlot));
    }

    public static void moveSlot(int amount) {
        if (cauldron == null || !cauldron.hasWorld()) return;
        selectSlot(selectedSlot + amount);

        final int usedSlots = CauldronHudRenderer.cauldron.getUsedSlotCount();
        if (usedSlots <= 1) return;
        toOffset += amount;

        if (MinecraftClient.getInstance().player == null) return;

        MinecraftClient.getInstance().player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), .4f, 2);
    }

    static void render(DrawContext drawContext, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        MatrixStack matrixStack = drawContext.getMatrices();
        if (client.world == null || client.player == null || !client.player.getMainHandStack().isOf(CherryItems.BLOSSOM_DUST)) {
            cauldron = null;
            return;
        }
        HitResult target = client.crosshairTarget;
        if (target == null || target.getType() != HitResult.Type.BLOCK) {
            cauldron = null;
            return;
        }

        BlockHitResult hitResult = (BlockHitResult) target;
        BlockPos pos = hitResult.getBlockPos();
        BlockState block = client.world.getBlockState(pos);

        if (block.isOf(CherryBlocks.CAULDRON)) {
            BlockEntity blockEntity = client.world.getBlockEntity(pos);
            if (!(blockEntity instanceof CherryCauldronBlockEntity entity)) {
                cauldron = null;
                return;
            }
            cauldron = entity;

            int usedSlots = CauldronHudRenderer.cauldron.getUsedSlotCount();

            if (usedSlots <= 0) return;

            if (toOffset >= usedSlots) {
                toOffset %= usedSlots;
            }

            if (selectedSlot >= usedSlots) {
                selectSlot(selectedSlot);
            }

            TextRenderer textRenderer = client.textRenderer;
            //drawContext.drawText(textRenderer, "" + selectedSlot, 8, 8, 0xffffffff, true);

            currentOffset = MathHelper.lerp(Math.min(1, tickCounter.getLastFrameDuration()), currentOffset, toOffset);

            if (Math.abs(toOffset - currentOffset) < 0.005) {
                currentOffset = 0;
                referenceSlot = selectedSlot;
                toOffset = 0;
            }

            final double base = Math.PI * 2 / usedSlots;
            final int radius = 64;

            DefaultedList<ItemStack> inventory = entity.getInventory();
            matrixStack.push();
            int halfW = drawContext.getScaledWindowWidth() / 2;
            int halfH = drawContext.getScaledWindowHeight() / 2;

            final int gr = 81;
            int guiX = halfW - gr;
            int guiY = halfH - gr;
            int guiRadius = gr * 2;

            double offset = base * (referenceSlot + currentOffset);
            drawGuiTexture(drawContext, SakuraCake.id("hud/cauldron_wheel"), guiX, guiY, 0, guiRadius, guiRadius);
            for (int i = 0; i < usedSlots; i++) {
                double angle = base * i - offset;
                double angle2 = angle - Math.PI / 2 + 0.000001; // Add a small value so movement doesn't look weird due to precision error
                int cos = (int) (Math.cos(angle2) * radius);
                int sin = (int) (Math.sin(angle2) * radius);
                int x = halfW + cos;
                int y = halfH + sin;

                matrixStack.push();
                matrixStack.translate(x, y, 0);
                matrixStack.multiply(RotationAxis.POSITIVE_Z.rotation((float) angle));
                ItemStack stack = inventory.get(i);
                drawContext.drawItem(stack, -8, -8);
                drawContext.drawItemInSlot(textRenderer, stack, -8, -8);
                matrixStack.pop();
            }

            RenderSystem.disableDepthTest();
            RenderSystem.enableDepthTest();
            matrixStack.pop();
        } else {
            cauldron = null;
        }
    }

    public static void drawGuiTexture(DrawContext context, Identifier texture, int x, int y, int z, int width, int height) {
        DrawContextAccessor accessor = (DrawContextAccessor) context;

        Sprite sprite = accessor.getGuiAtlasManager().getSprite(texture);
        Scaling scaling = accessor.getGuiAtlasManager().getScaling(sprite);
        if (scaling instanceof Scaling.Stretch) {
            drawSprite(context, sprite, x, y, z, width, height);
        } else {
            context.drawGuiTexture(texture, x, y, z, width, height);
        }
    }

    private static void drawSprite(DrawContext context, Sprite sprite, int x, int y, int z, int width, int height) {
        if (width != 0 && height != 0) {
            drawTexturedQuad(context, sprite.getAtlasId(), x, x + width, y, y + height, z, sprite.getMinU(), sprite.getMaxU(), sprite.getMinV(), sprite.getMaxV());
        }
    }

    public static void drawTexturedQuad(DrawContext context, Identifier texture, int x1, int x2, int y1, int y2, int z, float u1, float u2, float v1, float v2) {
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix4f, (float) x1, (float) y1, (float) z).texture(u1, v1);
        bufferBuilder.vertex(matrix4f, (float) x1, (float) y2, (float) z).texture(u1, v2);
        bufferBuilder.vertex(matrix4f, (float) x2, (float) y2, (float) z).texture(u2, v2);
        bufferBuilder.vertex(matrix4f, (float) x2, (float) y1, (float) z).texture(u2, v1);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    }
}
