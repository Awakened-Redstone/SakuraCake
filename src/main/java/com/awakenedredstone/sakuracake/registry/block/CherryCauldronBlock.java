package com.awakenedredstone.sakuracake.registry.block;

import com.awakenedredstone.sakuracake.SakuraCake;
import com.awakenedredstone.sakuracake.data.PlayerData;
import com.awakenedredstone.sakuracake.duck.CauldronDrop;
import com.awakenedredstone.sakuracake.event.WorldEvents;
import com.awakenedredstone.sakuracake.registry.CherryBlockEntities;
import com.awakenedredstone.sakuracake.registry.CherryItems;
import com.awakenedredstone.sakuracake.registry.CherrySounds;
import com.awakenedredstone.sakuracake.registry.block.entity.CherryCauldronBlockEntity;
import com.awakenedredstone.sakuracake.registry.entity.FloatingItemEntity;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CherryCauldronBlock extends BlockWithEntity {
    public static final EnumProperty<FillState> STATE = EnumProperty.of("state", FillState.class);
    public static final BooleanProperty LOCKED = BooleanProperty.of("locked");
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
        this.setDefaultState(this.stateManager.getDefaultState().with(STATE, FillState.EMPTY).with(LOCKED, false));
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(STATE).add(LOCKED);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!hasWater(state) && stack.isOf(Items.WATER_BUCKET) && !state.get(LOCKED)) {
            player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(Items.BUCKET)));

            boolean litCampfire = isLitCampfire(world.getBlockState(pos.down()));
            world.setBlockState(pos, state.with(STATE, litCampfire ? FillState.BOILING : FillState.FILLED));

            world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
            world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
            return ItemActionResult.SUCCESS;
        }

        if (hasWater(state) && stack.isOf(Items.BUCKET) && !state.get(LOCKED)) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(Items.WATER_BUCKET)));
            world.setBlockState(pos, state.with(STATE, FillState.EMPTY));
            world.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f);
            world.emitGameEvent(null, GameEvent.FLUID_PICKUP, pos);

            if (blockEntity instanceof CherryCauldronBlockEntity entity) {
                entity.dropInventory(false);
                entity.markDirtyAndNotify();
            }
            return ItemActionResult.SUCCESS;
        }

        if (state.get(STATE).equals(FillState.BOILING) && stack.isOf(CherryItems.WAND)) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (!(blockEntity instanceof CherryCauldronBlockEntity block)) return ItemActionResult.FAIL;

            if (block.isLocked()) {
                player.sendMessage(Text.translatable("container.isLocked", Text.translatable("block.sakuracake.cauldron")).formatted(Formatting.RED), true);
                return ItemActionResult.SUCCESS;
            }

            Optional<ItemStack> craft = block.craft();
            if (craft.isPresent()) {

                Vec3d center = Vec3d.ofCenter(pos);
                FloatingItemEntity itemEntity = new FloatingItemEntity(world, center.getX(), center.getY() - 0.1, center.getZ(), craft.get());
                itemEntity.setToDefaultPickupDelay();
                CauldronDrop.cast(itemEntity).sakuraCake$setCauldronDrop(true);
                block.reduceAll(1);
                block.markDirtyAndNotify();
                world.spawnEntity(itemEntity);

                world.playSoundAtBlockCenter(pos, CherrySounds.CAULDRON_CRAFT, SoundCategory.BLOCKS, 1, 1, true);
            }

            return ItemActionResult.SUCCESS;
        }

        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }

        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (!(blockEntity instanceof CherryCauldronBlockEntity entity)) {
            return ActionResult.FAIL;
        }

        if (entity.isEmpty()) {
            return ActionResult.FAIL;
        }

        if (entity.isLocked()) {
            player.sendMessage(Text.translatable("container.isLocked", Text.translatable("block.sakuracake.cauldron")).formatted(Formatting.RED), true);
            return ActionResult.SUCCESS;
        }

        int slot = Math.min(entity.getUsedSlotCount() - 1, ((AttachmentTarget) player).getAttachedOrCreate(PlayerData.CAULDRON_SLOT));

        WorldEvents.syncEvent(world, SakuraCake.id("empty_cauldron"), pos, 0);
        if (!entity.dropSlot(slot).isEmpty()) {
            entity.markDirtyAndNotify();
        }

        /*Optional<ItemStack> recipeOutput = entity.craft();
        if (recipeOutput.isPresent()) {
            ItemStack stack = recipeOutput.get();

            for (ItemStack item : entity.getItems()) {
                item.setCount(0);
            }

            Vec3d center = Vec3d.ofCenter(pos);
            ItemEntity itemEntity = new ItemEntity(world, center.getX(), center.getY() + 0.5, center.getZ(), stack);
            itemEntity.setToDefaultPickupDelay();
            world.spawnEntity(itemEntity);

            return ActionResult.SUCCESS;
        }*/

        return ActionResult.SUCCESS;
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        ItemScatterer.onStateReplaced(state, newState, world, pos);
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CherryCauldronBlock.createCodec(CherryCauldronBlock::new);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.DOWN && hasWater(state)) {
            return state.with(STATE, isLitCampfire(neighborState) ? FillState.BOILING : FillState.FILLED);
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    public static boolean hasWater(BlockState state) {
        FillState fillState = state.get(STATE);
        return fillState == FillState.FILLED || fillState == FillState.BOILING;
    }

    public static boolean isLitCampfire(BlockState state) {
        return state.isIn(BlockTags.CAMPFIRES) && state.get(CampfireBlock.LIT);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        boolean isInWater = fluidState.getFluid() == Fluids.WATER;
        BlockState blockState = super.getDefaultState();
        return blockState.with(STATE, isInWater ? FillState.IN_WATER : FillState.EMPTY);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        if (state.get(STATE).equals(FillState.IN_WATER)) {
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

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CherryCauldronBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (state.get(STATE).equals(FillState.BOILING)) {
            if (world.isClient) {
                return validateTicker(type, CherryBlockEntities.CAULDRON, CherryCauldronBlockEntity::tickClient);
            } else {
                return validateTicker(type, CherryBlockEntities.CAULDRON, CherryCauldronBlockEntity::tickServer);
            }
        } else {
            if (world.isClient) {
                return validateTicker(type, CherryBlockEntities.CAULDRON, CherryCauldronBlockEntity::tickColdClient);
            } else {
                return validateTicker(type, CherryBlockEntities.CAULDRON, CherryCauldronBlockEntity::tickColdServer);
            }
        }
    }

    public enum FillState implements StringIdentifiable {
        EMPTY,
        FILLED,
        BOILING,
        IN_WATER;

        @Override
        public String asString() {
            return name().toLowerCase();
        }
    }
}
