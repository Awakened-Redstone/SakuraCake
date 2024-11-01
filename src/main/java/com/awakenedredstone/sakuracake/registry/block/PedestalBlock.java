package com.awakenedredstone.sakuracake.registry.block;

import com.awakenedredstone.sakuracake.SakuraCake;
import com.awakenedredstone.sakuracake.event.WorldEvents;
import com.awakenedredstone.sakuracake.registry.CherryBlockEntities;
import com.awakenedredstone.sakuracake.registry.CherryBlocks;
import com.awakenedredstone.sakuracake.registry.CherryItems;
import com.awakenedredstone.sakuracake.registry.block.entity.PedestalBlockEntity;
import com.awakenedredstone.sakuracake.util.PositionStepper;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public class PedestalBlock extends BlockWithEntity implements Waterloggable {
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final IntProperty ROTATION = IntProperty.of("rotation", 0, 7);
    private static final int MULT =  (RotationPropertyHelper.getMax() + 1) / 8;
    public static final PositionStepper<BlockPos> POSITIONS = PositionStepper.of(
      pos -> pos,
      pos -> pos.south(4),
      pos -> pos.south(3).west(3),
      pos -> pos.west(4),
      pos -> pos.west(3).north(3),
      pos -> pos.north(4),
      pos -> pos.north(3).east(3),
      pos -> pos.east(4),
      pos -> pos.east(3).south(3)
    );

    public static final VoxelShape OUTLINE = VoxelShapes.union(
      createCuboidShape(0, 14, 0, 16, 16, 16),
      createCuboidShape(1, 13, 1, 15, 14, 15),
      createCuboidShape(4, 2, 4, 12, 13, 12),
      createCuboidShape(1, 0, 1, 15, 2, 15)
    );

    public PedestalBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState()
          .with(WATERLOGGED, false)
          .with(ROTATION, 0)
        );
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        buildFormation(world, pos);
        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        ItemScatterer.onStateReplaced(state, newState, world, pos);

        if (world.getBlockEntity(pos) instanceof PedestalBlockEntity entity && entity.getMasterPedestal() != null) {
            buildFormation(world, entity.getMasterPedestal());
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        boolean isAir = world.getBlockState(sourcePos).isAir();
        if (!isAir && sourcePos.equals(pos.up()) && world.getBlockEntity(pos) instanceof PedestalBlockEntity entity) {
            entity.dropItem();
            entity.markDirtyAndNotify();
            if (entity.getMasterPedestal() != null) {
                buildFormation(world, entity.getMasterPedestal());
            }
        }
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!(world.getBlockEntity(pos) instanceof PedestalBlockEntity entity)) return ItemActionResult.FAIL;
        if (stack.isEmpty()) return super.onUseWithItem(stack, state, world, pos, player, hand, hit);

        if (!world.getBlockState(pos.up()).isAir()) {
            return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
        }

        if (stack.isOf(CherryItems.WAND) && player.isSneaking()) {
            buildFormation(world, pos);
        }

        if (ItemStack.areEqual(entity.getStack(), stack.copy().copyWithCount(1))) return ItemActionResult.FAIL;
        if (world.isClient) return ItemActionResult.SUCCESS;

        WorldEvents.syncEvent(world, SakuraCake.id("insert_into_pedestal"), pos, 0);
        if (!player.isCreative()) entity.dropItem();
        entity.setStack(stack.splitUnlessCreative(1, player));
        entity.markDirtyAndNotify();

        return ItemActionResult.SUCCESS;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!(world.getBlockEntity(pos) instanceof PedestalBlockEntity entity)) return ActionResult.FAIL;

        if (player.isSneaking()) {
            if (entity.canCraft()) {
                entity.setCrafting();
            } else {
                buildFormation(world, pos);
            }
            return ActionResult.SUCCESS;
        }

        if (entity.getStack().isEmpty()) return ActionResult.FAIL;
        if (world.isClient()) return ActionResult.SUCCESS;

        WorldEvents.syncEvent(world, SakuraCake.id("empty_pedestal"), pos, 0);
        entity.dropItem();
        entity.markDirtyAndNotify();

        return ActionResult.SUCCESS;
    }

    public static void buildFormation(World world, BlockPos masterPos) {
        if (world.isClient()) return;

        boolean success = true;
        for (BlockPos pos : POSITIONS.getPositions(masterPos)) {
            BlockState state = world.getBlockState(pos);
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (!world.getBlockState(pos.up()).isAir()) {
                success = false;
                break;
            }
            if (state.isOf(CherryBlocks.PEDESTAL) && blockEntity instanceof PedestalBlockEntity) continue;
            success = false;
            break;
        }

        int i = -1;
        for (BlockPos pos : POSITIONS.getPositions(masterPos)) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof PedestalBlockEntity entity) {
                entity.setMasterPedestal(success ? masterPos : null);
                entity.setIndex(success ? i++ : -1);
                if (!success) entity.reset();
                entity.markDirtyAndNotify();
            }
        }
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return createCodec(PedestalBlock::new);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return OUTLINE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return OUTLINE;
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        return super.getPlacementState(ctx)
          .with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER)
          .with(ROTATION, RotationPropertyHelper.fromYaw(ctx.getPlayerYaw()) / MULT);
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(ROTATION, rotation.rotate(state.get(ROTATION), 7));
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.with(ROTATION, mirror.mirror(state.get(ROTATION), 7));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(WATERLOGGED);
        builder.add(ROTATION);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PedestalBlockEntity(pos, state);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        if (state.get(WATERLOGGED)) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient()) {
            return validateTicker(type, CherryBlockEntities.PEDESTAL, PedestalBlockEntity::tickClient);
        } else {
            return validateTicker(type, CherryBlockEntities.PEDESTAL, PedestalBlockEntity::tickServer);
        }
    }
}
