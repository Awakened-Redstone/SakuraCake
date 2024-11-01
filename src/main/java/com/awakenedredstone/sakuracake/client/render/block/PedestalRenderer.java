package com.awakenedredstone.sakuracake.client.render.block;

import com.awakenedredstone.sakuracake.SakuraCake;
import com.awakenedredstone.sakuracake.registry.block.entity.PedestalBlockEntity;
import net.fabricmc.fabric.api.client.model.loading.v1.FabricBakedModelManager;
import net.minecraft.block.SporeBlossomBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

public class PedestalRenderer implements BlockEntityRenderer<PedestalBlockEntity> {
    public static final Identifier CUBE_MODEL = SakuraCake.id("entity/pedestal/magicube");
    private final BlockEntityRendererFactory.Context context;

    public PedestalRenderer(BlockEntityRendererFactory.Context context) {
        this.context = context;
    }

    @Override
    public void render(PedestalBlockEntity entity, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        float time = (entity.getWorld().getTime() + tickDelta) % ((float) Math.PI * 2 * 100);

        boolean renderCube = entity.hasMasterPedestal() && !entity.isMasterPedestal();

        if (entity.getStack().isEmpty() && renderCube) {
            matrixStack.push();
            VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getSolid());

            double next = Math.sin((time) / 20f + entity.getIndex());

            matrixStack.translate(0, 1.25, 0);
            matrixStack.translate(0, next / 8, 0);

            BakedModel model = ((FabricBakedModelManager) MinecraftClient.getInstance().getBakedModelManager()).getModel(CUBE_MODEL);

            context.getRenderManager().getModelRenderer().render(
              entity.getWorld(), model, entity.getCachedState(),
              entity.getPos(), matrixStack, buffer, false,
              Random.create(), 42L, overlay
            );

            matrixStack.pop();
        }

        if (!entity.getStack().isEmpty() && renderCube) {
            matrixStack.push();
            VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getSolid());

            Vec3d position = PedestalBlockEntity.MATRIX[entity.getIndex()];
            BlockPos master = entity.getMasterPedestal().up(3);
            float fixPos = 5 / 16f;
            Vec3d mainPos = Vec3d.of(master.subtract(entity.getPos()))
              .add(position.getX() / 16, position.getY() / 16, position.getZ() / 16)
              .subtract(fixPos, fixPos, fixPos);

            matrixStack.translate(mainPos.getX(), mainPos.getY(), mainPos.getZ());

            BakedModel model = ((FabricBakedModelManager) MinecraftClient.getInstance().getBakedModelManager()).getModel(CUBE_MODEL);

            context.getRenderManager().getModelRenderer().render(
              entity.getWorld(), model, entity.getCachedState(),
              entity.getPos(), matrixStack, buffer, false,
              Random.create(), 42L, overlay
            );

            matrixStack.pop();
        }

        boolean renderItem = (renderCube && !entity.isCrafting()) || entity.getIndex() == -1;
        if (!entity.getStack().isEmpty() && renderItem) {
            matrixStack.push();
            float unique;
            if (entity.getIndex() != -1) {
                unique = (float) (Math.PI / 4 * entity.getIndex());
            } else {
                unique = entity.getPos().hashCode();
            }
            //float offset = MathHelper.sin(time / 10.0f) * 0.05f + 0.025f;
            BakedModel model = context.getItemRenderer().getModel(entity.getStack(), entity.getWorld(), null, 0);

            float bobbing = MathHelper.sin(time / 10 + unique) * 0.075f + 0.2f;
            //PedestalBlockEntity.LOGGER.info("Client: {}", bobbing);
            float transform = model.getTransformation().getTransformation(ModelTransformationMode.GROUND).scale.y();
            matrixStack.translate(0, bobbing + 0.25 * transform, 0);

            //matrixStack.translate(translate.x() + 0.5, translate.y() + 1 + offset, translate.z() + 0.5);
            matrixStack.translate(0.5, 1, 0.5);
            if (entity.getStack().getItem() instanceof BlockItem blockItem) {
                if (blockItem.getBlock() instanceof SporeBlossomBlock /*TODO: Add option to disable the offset for resourcepack compatibility*/) {
                    matrixStack.scale(1.5F, 1.5F, 1.5F);
                    matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
                    matrixStack.translate(0, -0.1, 0);
                }
                matrixStack.translate(0, -0.15, 0);
            }
            int age = (int) (entity.getWorld().getTime());
            float itemRotation = (age + tickDelta) / 30.0F;
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotation(itemRotation));
            ItemEntityRenderer.renderStack(context.getItemRenderer(), matrixStack, vertexConsumers, light, entity.getStack(), model, model.hasDepth(), Random.create());
            //context.getItemRenderer().renderItem(entity.getStack(), ModelTransformationMode.GROUND, false, matrixStack, vertexConsumers, light, overlay, model);
            matrixStack.pop();
        }
    }
}
