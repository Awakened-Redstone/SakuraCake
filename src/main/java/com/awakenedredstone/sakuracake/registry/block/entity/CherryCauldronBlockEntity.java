package com.awakenedredstone.sakuracake.registry.block.entity;

import com.awakenedredstone.sakuracake.SakuraCake;
import com.awakenedredstone.sakuracake.client.SakuraCakeClient;
import com.awakenedredstone.sakuracake.duck.CauldronDrop;
import com.awakenedredstone.sakuracake.event.WorldEvents;
import com.awakenedredstone.sakuracake.recipe.MixinRecipe;
import com.awakenedredstone.sakuracake.recipe.input.ItemStackRecipeInput;
import com.awakenedredstone.sakuracake.registry.CherryBlockEntities;
import com.awakenedredstone.sakuracake.registry.CherryParticles;
import com.awakenedredstone.sakuracake.registry.CherryRecipeTypes;
import com.awakenedredstone.sakuracake.registry.CherryDataComponentTypes;
import com.awakenedredstone.sakuracake.util.PacketUtil;
import com.bawnorton.configurable.Configurable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.Unit;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Configurable("Cauldron")
public class CherryCauldronBlockEntity extends BlockEntity implements SidedInventory {
    @Configurable(max = Short.MAX_VALUE)
    public static short boilTime = 200;
    private static final int[] NO_SLOT = new int[0];

    @Environment(EnvType.CLIENT)
    private short clientBoilingTimer = 0;
    private short boilingTimer = 0;
    private short cooldown = 0;
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(16, ItemStack.EMPTY);
    private final RecipeManager.MatchGetter<ItemStackRecipeInput, MixinRecipe> matchGetter = RecipeManager.createCachedMatchGetter(CherryRecipeTypes.MIXIN);

    public CherryCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(CherryBlockEntities.CAULDRON, pos, state);
    }

    //region Ticking
    //region Cold
    public static void tickColdClient(World world, BlockPos pos, BlockState state, CherryCauldronBlockEntity block) {
        if (block.clientBoilingTimer < boilTime * 2) {
            Random random = world.getRandom();
            float x = random.nextFloat() * .6875f + .15625f;
            float z = random.nextFloat() * .6875f + .15625f;

            if (block.clientBoilingTimer > boilTime) {
                if (random.nextInt(boilTime) < block.clientBoilingTimer - boilTime) {
                    world.addImportantParticle(CherryParticles.CAULDRON_BUBBLE, pos.getX() + x, pos.getY() + .38/*.935*/, pos.getZ() + z, 0, 0, 0);
                }
            }
        }

        if (block.clientBoilingTimer > 0) {
            block.clientBoilingTimer = (short) Math.max(0, block.clientBoilingTimer - 2);
        }
    }

    public static void tickColdServer(World world, BlockPos pos, BlockState state, CherryCauldronBlockEntity block) {
        if (block.boilingTimer > 0) {
            block.boilingTimer = (short) Math.max(0, block.boilingTimer - 2);
        }
    }
    //endregion

    //region Boiling
    public static void tickClient(World world, BlockPos pos, BlockState state, CherryCauldronBlockEntity block) {
        if (block.removed) return;

        Random random = world.getRandom();
        float x = random.nextFloat() * .6875f + .15625f;
        float z = random.nextFloat() * .6875f + .15625f;

        short timer = block.clientBoilingTimer;
        if (timer < boilTime * 2) {
            block.clientBoilingTimer++;
            if (timer > boilTime) {
                if (random.nextInt(boilTime) < block.clientBoilingTimer - boilTime) {
                    world.addImportantParticle(CherryParticles.CAULDRON_BUBBLE, pos.getX() + x, pos.getY() + .38/*.9375*/, pos.getZ() + z, 0, 0, 0);
                }
            }
        } else if (SakuraCakeClient.getTicks() % 2 == 0) {
            world.addImportantParticle(CherryParticles.CAULDRON_BUBBLE, pos.getX() + x, pos.getY() + .38/*.9375*/, pos.getZ() + z, 0, 0, 0);
        }
    }

    public static void tickServer(World world, BlockPos pos, BlockState state, CherryCauldronBlockEntity block) {
        if (block.removed) return;

        if (block.boilingTimer >= boilTime && block.cooldown <= 0) {
            List<ItemEntity> items = block.getDroppedItems();
            block.cooldown = 5;
            if (!items.isEmpty() && extract(block, items.getFirst())) {
                WorldEvents.syncEvent(world, SakuraCake.id("absorb_item"), pos, 0);
                block.markDirtyAndNotify();
            }
        } else {
            block.cooldown = (short) Math.max(0, block.cooldown - 1);
        }

        if (block.boilingTimer < boilTime) {
            block.boilingTimer++;
        }
    }
    //endregion
    //endregion

    private List<ItemEntity> getDroppedItems() {
        return world.getEntitiesByType(TypeFilter.instanceOf(ItemEntity.class), getInnerBox(), itemEntity -> !CauldronDrop.cast(itemEntity).sakuraCake$isCauldronDrop() && itemEntity.isOnGround());
    }

    private Box getInnerBox() {
        return new Box(pos.getX() + .125, pos.getY() + .375, pos.getZ() + .125, pos.getX() + .875, pos.getY() + .9375/*.9375*/, pos.getZ() + .875);
    }

    public Optional<ItemStack> craft() {
        ItemStackRecipeInput input = new ItemStackRecipeInput(new ArrayList<>(inventory));
        Optional<RecipeEntry<MixinRecipe>> recipeEntry = this.matchGetter.getFirstMatch(input, this.world);
        if (recipeEntry.isPresent()) {
            return recipeEntry.map(recipe -> recipe.value().craft(input, world.getRegistryManager()));
        }
        return Optional.empty();
    }

    public void markDirtyAndNotify() {
        markDirty();
        getWorld().updateListeners(getPos(), getCachedState(), getCachedState(), Block.NOTIFY_LISTENERS);
    }

    //region Inventory
    public DefaultedList<ItemStack> getInventory() {
        return inventory;
    }

    @Override
    public int size() {
        return this.inventory.size();
    }

    @Override
    public boolean isEmpty() {
        return getUsedSlotCount() <= 0;
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.inventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(this.inventory, slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return this.inventory.remove(slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.inventory.set(slot, stack);
    }

    public int firstEmptySlot() {
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.get(i);
            if (stack.isEmpty()) {
                return i;
            }
        }

        return -1;
    }

    public int getUsedSlotCount() {
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.get(i);
            if (stack.isEmpty()) {
                return i;
            }
        }

        return inventory.size();
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return NO_SLOT;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return false;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return false;
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return false;
    }

    @Override
    public void clear() {
        this.inventory.clear();
    }

    public void reduceAll(int amount) {
        for (ItemStack stack : this.inventory) {
            stack.decrement(1);
        }
    }

    public void dropInventory(boolean noPickup) {
        for (ItemStack stack : inventory) {
            if (stack.isEmpty()) continue;

            Vec3d center = Vec3d.ofCenter(pos);
            ItemEntity itemEntity = new ItemEntity(world, center.getX(), center.getY() - 0.1, center.getZ(), stack);
            itemEntity.setToDefaultPickupDelay();
            CauldronDrop.cast(itemEntity).sakuraCake$setCauldronDrop(noPickup);
            world.spawnEntity(itemEntity);
        }
        this.inventory.clear();
    }

    public ItemStack dropSlot(int slot) {
        ItemStack stack = this.inventory.get(slot);
        if (stack.isEmpty()) return stack;

        Vec3d center = Vec3d.ofCenter(pos);
        ItemEntity itemEntity = new ItemEntity(world, center.getX(), center.getY() - 0.1, center.getZ(), stack);
        itemEntity.setToDefaultPickupDelay();
        CauldronDrop.cast(itemEntity).sakuraCake$setCauldronDrop(true);
        this.inventory.set(slot, ItemStack.EMPTY);
        world.spawnEntity(itemEntity);

        rebuildInventory();

        return stack;
    }

    private void rebuildInventory() {
        List<ItemStack> old = new ArrayList<>(this.inventory);
        this.inventory.clear();

        int index = 0;
        for (ItemStack stack : old) {
            if (stack.isEmpty()) continue;
            this.inventory.set(index++, stack);
        }
    }

    public static boolean extract(Inventory inventory, ItemEntity itemEntity) {
        boolean bl = false;
        ItemStack itemStack = itemEntity.getStack().copy();
        ItemStack itemStack2 = transfer(inventory, itemStack);
        if (itemStack2.isEmpty()) {
            bl = true;
            itemEntity.setStack(ItemStack.EMPTY);
            itemEntity.discard();
        } else {
            itemEntity.setStack(itemStack2);
        }

        return bl;
    }

    public static ItemStack transfer(Inventory to, ItemStack stack) {
        int j = to.size();

        for (int i = 0; i < j && !stack.isEmpty(); i++) {
            stack = transfer(to, stack, i);
        }

        return stack;
    }

    private static ItemStack transfer(Inventory to, ItemStack stack, int slot) {
        ItemStack itemStack = to.getStack(slot);
        boolean success = false;
        if (itemStack.isEmpty()) {
            to.setStack(slot, stack);
            stack = ItemStack.EMPTY;
            success = true;
        } else if (canMergeItems(itemStack, stack)) {
            int i = stack.getMaxCount() - itemStack.getCount();
            int j = Math.min(stack.getCount(), i);
            stack.decrement(j);
            itemStack.increment(j);
            success = j > 0;
        }

        if (success) {
            to.markDirty();
        }

        return stack;
    }

    public static boolean canMergeItems(ItemStack first, ItemStack second) {
        return first.getCount() <= first.getMaxCount() && ItemStack.areItemsAndComponentsEqual(first, second);
    }
    //endregion

    //region NBT Data
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putShort("BoilingTime", boilingTimer);
        nbt.putShort("Cooldown", cooldown);
        Inventories.writeNbt(nbt, this.inventory, registryLookup);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        boilingTimer = nbt.getShort("BoilingTime");
        cooldown = nbt.getShort("Cooldown");
        this.inventory.clear();
        Inventories.readNbt(nbt, this.inventory, registryLookup);
    }

    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return this.createNbt(registryLookup);
    }
    //endregion
}
