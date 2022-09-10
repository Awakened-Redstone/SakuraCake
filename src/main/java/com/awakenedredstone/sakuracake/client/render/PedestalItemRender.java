package com.awakenedredstone.sakuracake.client.render;

import com.awakenedredstone.sakuracake.block.core.InventoryBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EndCrystalEntityRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;

public class PedestalItemRender<T extends InventoryBlockEntity> implements BlockEntityRenderer<T> {

    public PedestalItemRender(BlockEntityRendererFactory.Context context) {
        super();
    }

    @Override
    public void render(T blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        World world = blockEntity.getWorld();
        if (world == null || blockEntity.isEmpty()) return;

        BlockState state = world.getBlockState(blockEntity.getPos());
        //if (!(state.getBlock() instanceof PedestalBlock)) return;

        ItemStack activeItem = blockEntity.getStack(0);

        matrices.push();
        MinecraftClient minecraft = MinecraftClient.getInstance();
        BakedModel model = minecraft.getItemRenderer().getModel(activeItem, world, null, 0);
        Vec3f translate = model.getTransformation().ground.translation;
        Block pedestal = state.getBlock();
        matrices.translate(translate.getX() + 0.5, translate.getY(), translate.getZ() + 0.5);
        if (activeItem.getItem() instanceof BlockItem) {
            matrices.scale(1.5F, 1.5F, 1.5F);
        } else {
            matrices.scale(1.25F, 1.25F, 1.25F);
        }
        int age = (int) (minecraft.world.getTime() % 314);
        float rotation = (age + tickDelta) / 25.0F + 6.0F;
        matrices.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion(rotation));
        minecraft.getItemRenderer().renderItem(activeItem, ModelTransformation.Mode.GROUND, false, matrices, vertexConsumers, light, overlay, model);
        matrices.pop();
    }
}
