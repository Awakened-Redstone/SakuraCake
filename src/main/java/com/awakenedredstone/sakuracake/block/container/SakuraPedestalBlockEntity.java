package com.awakenedredstone.sakuracake.block.container;

import com.awakenedredstone.sakuracake.SakuraBlockEntities;
import com.awakenedredstone.sakuracake.block.SakuraPedestalBlock;
import com.awakenedredstone.sakuracake.block.core.InventoryBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SakuraPedestalBlockEntity extends InventoryBlockEntity {
    private ItemStack item = ItemStack.EMPTY;

    public SakuraPedestalBlockEntity(BlockPos pos, BlockState state) {
        super(SakuraBlockEntities.SAKURA_PEDESTAL_BLOCK_ENTITY, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, SakuraPedestalBlockEntity blockEntity, SakuraPedestalBlock block) {}

    @Override
    protected void writeNbt(NbtCompound nbt) {
        if (item != ItemStack.EMPTY) {
            nbt.put("item", item.writeNbt(new NbtCompound()));
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        if (nbt.contains("item")) {
            NbtCompound itemTag = nbt.getCompound("item");
            item = ItemStack.fromNbt(itemTag);
        }
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return item.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return item;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return removeStack(slot);
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack copy = item.copy();
        item = ItemStack.EMPTY;
        return copy;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        item = stack.split(1);
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return isEmpty();
    }

    @Override
    public void clear() {
        item = ItemStack.EMPTY;
    }
}
