package com.awakenedredstone.sakuracake.registry.block;

import com.awakenedredstone.sakuracake.registry.CherryBlockEntities;
import com.awakenedredstone.sakuracake.registry.block.entity.FocusedAuraBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FocusedAuraBlock extends BlockWithEntity {
    public static final MapCodec<FocusedAuraBlock> CODEC = createCodec(FocusedAuraBlock::new);

    public FocusedAuraBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    protected boolean canReplace(BlockState state, ItemPlacementContext context) {
        return true;
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_TRIAL_SPAWNER_BREAK, SoundCategory.BLOCKS, 1, 1);
    }

    @Override
    protected VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.empty();
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new FocusedAuraBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient) {
            return validateTicker(type, CherryBlockEntities.FOCUSED_AURA, FocusedAuraBlockEntity::tickClient);
        } else {
            return null;
        }
    }
}
