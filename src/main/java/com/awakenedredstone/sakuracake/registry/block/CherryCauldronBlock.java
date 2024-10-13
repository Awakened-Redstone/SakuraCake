package com.awakenedredstone.sakuracake.registry.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class CherryCauldronBlock extends Block {
    public static final EnumProperty<FillState> FILLED = EnumProperty.of("filled", FillState.class);
    //region Hitbox
    private static final VoxelShape INNER_BOX = createCuboidShape(2, 6, 2, 14, 16, 14);
    protected static final VoxelShape OUTLINE_SHAPE = VoxelShapes.combineAndSimplify(
      VoxelShapes.fullCube(),
      VoxelShapes.union(
        // Top ring
        createCuboidShape(0, 13, 0, 1, 14, 16),
        createCuboidShape(15, 13, 0, 16, 14, 16),
        createCuboidShape(0, 13, 0, 16, 14, 1),
        createCuboidShape(0, 13, 15, 16, 14, 16),

        // Around feet
        createCuboidShape(0, 0, 0, 1, 3, 16),
        createCuboidShape(15, 0, 0, 16, 3, 16),
        createCuboidShape(0, 0, 0, 16, 3, 1),
        createCuboidShape(0, 0, 15, 16, 3, 16),

        // Between feet
        createCuboidShape(3, 0, 3, 13, 3, 13),

        createCuboidShape(5, 0, 1, 11, 3, 15),
        createCuboidShape(1, 0, 5, 15, 3, 11),
        INNER_BOX
      ),
      BooleanBiFunction.ONLY_FIRST
    );
    //endregion

    public CherryCauldronBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FILLED, FillState.EMPTY));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FILLED);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (state.get(FILLED).equals(FillState.EMPTY) && stack.isOf(Items.WATER_BUCKET)) {
            player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(Items.BUCKET)));
            world.setBlockState(pos, state.with(FILLED, FillState.FILLED));
            //TODO: create ticker
            world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
            world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
            return ItemActionResult.SUCCESS;
        }

        if (state.get(FILLED).equals(FillState.FILLED) && stack.isOf(Items.BUCKET)) {
            player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(Items.WATER_BUCKET)));
            world.setBlockState(pos, state.with(FILLED, FillState.EMPTY));
            //TODO: kill ticker
            world.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f);
            world.emitGameEvent(null, GameEvent.FLUID_PICKUP, pos);
            return ItemActionResult.SUCCESS;
        }

        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    @Override
    protected MapCodec<? extends Block> getCodec() {
        return CherryCauldronBlock.createCodec(CherryCauldronBlock::new);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        boolean isInWater = fluidState.getFluid() == Fluids.WATER;
        BlockState blockState = super.getDefaultState();
        return blockState.with(FILLED, isInWater ? FillState.IN_WATER : FillState.EMPTY);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        if (state.get(FILLED).equals(FillState.IN_WATER)) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return OUTLINE_SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return OUTLINE_SHAPE;
    }

    public enum FillState implements StringIdentifiable {
        EMPTY,
        FILLED,
        IN_WATER;

        @Override
        public String asString() {
            return name().toLowerCase();
        }
    }
}
