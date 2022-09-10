package com.awakenedredstone.sakuracake.block;

import com.awakenedredstone.sakuracake.SakuraBlockEntities;
import com.awakenedredstone.sakuracake.block.container.SakuraPedestalBlockEntity;
import com.awakenedredstone.sakuracake.block.core.HorizontalFacingBlockWithTicking;
import com.awakenedredstone.sakuracake.util.VoxelShapeUtils;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class SakuraPedestalBlock extends HorizontalFacingBlockWithTicking implements BlockEntityProvider {
    public SakuraPedestalBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        return VoxelShapeUtils.combine(
                createCuboidShape(2, 0, 2, 14, 1, 14),
                createCuboidShape(5, 1, 5, 11, 12, 11),
                createCuboidShape(2, 14, 2, 14, 16, 14),
                createCuboidShape(4, 12, 4, 12, 14, 12)
        );
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            player.sendMessage(Text.translatable(player.getStackInHand(hand).getTranslationKey()));
            SakuraPedestalBlockEntity blockEntity = (SakuraPedestalBlockEntity) world.getBlockEntity(pos);
            if (blockEntity == null) {
                player.sendMessage(Text.literal("(SakuraCake) Error: How in the world you clicked on a pedestal without the block entity"));
                return super.onUse(state, world, pos, player, hand, hit);
            }
            blockEntity.setStack(0, player.getStackInHand(hand));
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(Properties.HORIZONTAL_FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(Properties.HORIZONTAL_FACING, ctx.getPlayerFacing().getOpposite());
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SakuraPedestalBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return BlockWithEntity.checkType(type, SakuraBlockEntities.SAKURA_PEDESTAL_BLOCK_ENTITY, (world1, pos, state1, be) -> {
            SakuraPedestalBlockEntity.tick(world1, pos, state1, be, this);
        });
    }
}
