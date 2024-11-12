package com.awakenedredstone.sakuracake.registry.block.entity;

import com.awakenedredstone.sakuracake.particle.AbsorbColorTransitionParticleEffect;
import com.awakenedredstone.sakuracake.recipe.ThaumicRecipe;
import com.awakenedredstone.sakuracake.recipe.input.PedestalRecipeInput;
import com.awakenedredstone.sakuracake.registry.CherryBlockEntities;
import com.awakenedredstone.sakuracake.registry.CherryBlocks;
import com.awakenedredstone.sakuracake.registry.CherryRecipeTypes;
import com.awakenedredstone.sakuracake.registry.block.PedestalBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SingleStackInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PedestalBlockEntity extends BlockEntity implements SingleStackInventory.SingleStackBlockEntityInventory {
    public static final Logger LOGGER = LoggerFactory.getLogger("Pedestal Block Entity");
    public static final Vec3d[] MATRIX = new Vec3d[]{
      new Vec3d(1.5, 8.5, 1.5),
      new Vec3d(1.5, 8.5, 8.5),
      new Vec3d(8.5, 8.5, 1.5),
      new Vec3d(8.5, 8.5, 8.5),

      new Vec3d(1.5, 1.5, 1.5),
      new Vec3d(1.5, 1.5, 8.5),
      new Vec3d(8.5, 1.5, 1.5),
      new Vec3d(8.5, 1.5, 8.5)
    };
    private final RecipeManager.MatchGetter<PedestalRecipeInput, ThaumicRecipe> matchGetter = RecipeManager.createCachedMatchGetter(CherryRecipeTypes.THAUMIC);
    private static final int CRAFT_TIME = 5;
    protected ItemStack stack = ItemStack.EMPTY;
    protected BlockPos masterPedestal = null;
    protected int index = -1;
    protected int crafting = -1;
    private boolean locked = false;

    public PedestalBlockEntity(BlockPos pos, BlockState state) {
        super(CherryBlockEntities.PEDESTAL, pos, state);
    }

    public static void tickClient(World world, BlockPos pos, BlockState state, PedestalBlockEntity block) {
    }

    public static void tickServer(World world, BlockPos pos, BlockState state, PedestalBlockEntity block) {
        if (!block.isMasterPedestal()) return;

        if (block.crafting > -1) {
            if (block.crafting >= CRAFT_TIME * 8) {
                if (block.crafting++ >= CRAFT_TIME * 9) {
                    Optional<ItemStack> itemStack = block.craft();
                    boolean success = itemStack.isPresent();
                    PedestalBlock.POSITIONS.forEach(pos, blockPos -> {
                        var blockEntity = world.getBlockEntity(blockPos);
                        if (blockEntity instanceof PedestalBlockEntity entity) {
                            entity.crafting = -1;
                            if (itemStack.isPresent()) {
                                entity.setStack(ItemStack.EMPTY);
                                entity.markDirtyAndNotify();
                            }
                        }
                    });

                    BlockPos masterPos = block.masterPedestal.up(3);
                    double fixPos = 0.5;

                    Vec3d start = Vec3d.ofCenter(pos).add(0, 0.75, 0);
                    Vec3d end = Vec3d.of(masterPos).add(fixPos, fixPos, fixPos);

                    double distance = Math.sqrt(start.squaredDistanceTo(end));
                    Vec3d direction = lookAt(start, end);

                    Vec3d color1 = Vec3d.unpackRgb(success ? 0xf4a6c9 : 0xff0000);
                    Vec3d color2 = Vec3d.unpackRgb(success? 0x90dff9 : 0xff0000);
                    double colorDistance = Math.sqrt(color1.squaredDistanceTo(color2));
                    Vec3d colorDirection = lookAt(color1, color2);

                    for (double i = 0; i < distance; i += 0.01 * distance) {
                        Vec3d particlePos = start.add(direction.multiply(i));
                        Vector3f particleColor = color1.add(colorDirection.multiply((i / distance) * colorDistance)).toVector3f();

                        ((ServerWorld) world).spawnParticles(
                          new AbsorbColorTransitionParticleEffect(particleColor, 0.5f),
                          particlePos.getX(), particlePos.getY(), particlePos.getZ(),
                          1,
                          0, 0, 0,
                          1
                        );
                    }

                    itemStack.ifPresent(block::setStack);

                    block.crafting = -1;
                    block.markDirtyAndNotify();
                    return;
                }
                return;
            }

            if (block.crafting++ % CRAFT_TIME == 0) {
                int index = (block.crafting - 1) / CRAFT_TIME;
                BlockPos blockPos = PedestalBlock.POSITIONS.get(index + 1, pos);

                BlockEntity blockEntity = world.getBlockEntity(blockPos);

                boolean success;
                if (blockEntity instanceof PedestalBlockEntity entity) {
                    if (!entity.isMasterPedestal()) {
                        entity.crafting = 1;
                        entity.markDirtyAndNotify();
                    }
                    success = true;
                } else {
                    success = false;
                }

                BlockPos masterPos = block.masterPedestal.up(3);
                double fixPos = 0.5;

                Vec3d start = Vec3d.ofCenter(blockPos).add(0, 0.75, 0);
                Vec3d end = Vec3d.of(masterPos).add(fixPos, fixPos, fixPos);

                double distance = Math.sqrt(start.squaredDistanceTo(end));
                Vec3d direction = lookAt(start, end);

                Vec3d color1 = Vec3d.unpackRgb(success ? 0xf4a6c9 : 0xff0000);
                Vec3d color2 = Vec3d.unpackRgb(success? 0x90dff9 : 0xff0000);
                double colorDistance = Math.sqrt(color1.squaredDistanceTo(color2));
                Vec3d colorDirection = lookAt(color1, color2);

                for (double i = 0; i < distance; i += 0.01 * distance) {
                    Vec3d particlePos = start.add(direction.multiply(i));
                    Vector3f particleColor = color1.add(colorDirection.multiply((i / distance) * colorDistance)).toVector3f();

                    ((ServerWorld) world).spawnParticles(
                      new AbsorbColorTransitionParticleEffect(particleColor, 0.5f),
                      particlePos.getX(), particlePos.getY(), particlePos.getZ(),
                      1,
                      0, 0, 0,
                      1
                    );
                }

                if (!success) {
                    block.crafting = -1;
                    block.markDirtyAndNotify();
                    PedestalBlock.POSITIONS.forEach(pos, blockPos1 -> {
                        var blockEntity1 = world.getBlockEntity(blockPos1);
                        if (blockEntity1 instanceof PedestalBlockEntity entity) {
                            entity.crafting = -1;
                            entity.markDirtyAndNotify();
                        }
                    });
                }
            }
        }
    }

    public void reset() {
        masterPedestal = null;
        index = -1;
        crafting = -1;
    }

    public void setMasterPedestal(BlockPos masterPedestal) {
        this.masterPedestal = masterPedestal;
    }

    public BlockPos getMasterPedestal() {
        return masterPedestal;
    }

    public boolean isMasterPedestal() {
        return this.pos.equals(masterPedestal);
    }

    public boolean hasMasterPedestal() {
        return masterPedestal != null;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setCrafting() {
        this.crafting = 0;
    }

    public boolean isCrafting() {
        return crafting > -1;
    }

    public boolean canCraft() {
        if (!isMasterPedestal() || stack.isEmpty()) return false;

        return craft().isPresent();
    }

    public Optional<ItemStack> craft() {
        if (!this.isMasterPedestal()) return Optional.empty();

        List<ItemStack> stacks = new ArrayList<>(8);
        for (BlockPos pos : PedestalBlock.POSITIONS.getPositions(masterPedestal)) {
            if (pos.equals(masterPedestal)) continue;
            BlockState state = world.getBlockState(pos);
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (state.isOf(CherryBlocks.PEDESTAL) && blockEntity instanceof PedestalBlockEntity pedestal) {
                stacks.add(pedestal.getStack());
                continue;
            }
            return Optional.empty();
        }

        if (stack.isEmpty()) return Optional.empty();

        PedestalRecipeInput input = new PedestalRecipeInput(stack, stacks);
        Optional<RecipeEntry<ThaumicRecipe>> recipeEntry = this.matchGetter.getFirstMatch(input, this.world);
        if (recipeEntry.isPresent()) {
            return recipeEntry.map(recipe -> recipe.value().craft(input, world.getRegistryManager()));
        }
        return Optional.empty();
    }

    public void markDirtyAndNotify() {
        markDirty();
        getWorld().updateListeners(getPos(), getCachedState(), getCachedState(), Block.NOTIFY_ALL);
    }

    //region Inventory
    public boolean isLocked() {
        return locked;
    }

    @Override
    public BlockEntity asBlockEntity() {
        return this;
    }

    @Override
    public ItemStack getStack() {
        return stack;
    }

    @Override
    public ItemStack decreaseStack(int count) {
        ItemStack itemStack = this.stack;
        this.setStack(ItemStack.EMPTY);
        return itemStack;
    }

    @Override
    public void setStack(ItemStack stack) {
        boolean playParticles = (this.stack.isEmpty() && !stack.isEmpty()) || (!this.stack.isEmpty() && stack.isEmpty());

        if (playParticles && index >= 0 && index < 8 && hasMasterPedestal() && !world.isClient()) {
            float time = (world.getTime()) % ((float) Math.PI * 2 * 100);
            double next = Math.sin((time) / 20f + index);

            Vec3d matrixPos = PedestalBlockEntity.MATRIX[index];

            BlockPos masterPos = masterPedestal.up(3);
            double fixPos = 3 / 16d;

            Vec3d start = Vec3d.ofCenter(pos).add(0, 1.25 + next / 8, 0);
            Vec3d end = Vec3d.of(masterPos)
              .add(matrixPos.getX() / 16, matrixPos.getY() / 16, matrixPos.getZ() / 16)
              .add(fixPos, fixPos, fixPos);

            castRay(new DustParticleEffect(Vec3d.unpackRgb(0x000000).toVector3f(), 0.5f), world, start, end);
        }

        this.stack = stack;
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return !this.locked && SingleStackBlockEntityInventory.super.canPlayerUse(player);
    }

    public static <T extends ParticleEffect> void castRay(T particle, World world, Vec3d start, Vec3d end) {
        if (world.isClient()) return;

        double distance = Math.sqrt(start.squaredDistanceTo(end));
        Vec3d direction = lookAt(start, end);

        for (double i = 0; i < distance; i += 0.01 * distance) {
            Vec3d pos = start.add(direction.multiply(i));

            ((ServerWorld) world).spawnParticles(
              particle,
              pos.getX(), pos.getY(), pos.getZ(),
              1,
              0, 0, 0,
              1
            );
        }
    }

    public static Vec3d lookAt(Vec3d anchor, Vec3d target) {
        double d = target.x - anchor.x;
        double e = target.y - anchor.y;
        double f = target.z - anchor.z;
        double g = Math.sqrt(d * d + f * f);
        float pitch = MathHelper.wrapDegrees((float) (-(MathHelper.atan2(e, g) * 57.2957763671875)));
        float yaw = MathHelper.wrapDegrees((float) (MathHelper.atan2(f, d) * 57.2957763671875) - 90.0f);
        return getRotationVector(pitch, yaw);
    }

    protected static Vec3d getRotationVector(float pitch, float yaw) {
        float f = pitch * ((float) Math.PI / 180);
        float g = -yaw * ((float) Math.PI / 180);
        float h = MathHelper.cos(g);
        float i = MathHelper.sin(g);
        float j = MathHelper.cos(f);
        float k = MathHelper.sin(f);
        return new Vec3d(i * j, -k, h * j);
    }

    @Override
    public int getMaxCountPerStack() {
        return 1;
    }

    public void dropItem() {
        if (stack.isEmpty()) return;

        Vec3d center = Vec3d.ofCenter(pos);
        Random rng = world.getRandom();
        double velX = rng.nextDouble() * 0.1 - 0.05;
        double velZ = rng.nextDouble() * 0.1 - 0.05;
        ItemEntity itemEntity = new ItemEntity(world, center.getX(), center.getY() + 0.6, center.getZ(), stack, velX, 0.15, velZ);
        itemEntity.setToDefaultPickupDelay();
        this.emptyStack();
        world.spawnEntity(itemEntity);
    }
    //endregion

    //region NBT data
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);

        if (!this.stack.isEmpty()) {
            nbt.put("item", this.stack.encode(registryLookup));
        }

        if (this.masterPedestal != null) {
            nbt.put("master", BlockPos.CODEC.encodeStart(registryLookup.getOps(NbtOps.INSTANCE), masterPedestal).getOrThrow());
            nbt.putByte("index", (byte) index);
        }

        if (this.crafting > -1) {
            nbt.putShort("crafting", (short) crafting);
        }

        if (this.locked) {
            nbt.putBoolean("Locked", true);
        }

        if (nbt.isEmpty()) {
            nbt.put("_", NbtByte.of((byte) 0));
        }
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        if (nbt.contains("item", NbtElement.COMPOUND_TYPE)) {
            this.stack = ItemStack.fromNbt(registryLookup, nbt.getCompound("item")).orElse(ItemStack.EMPTY);
        } else {
            this.stack = ItemStack.EMPTY;
        }

        if (nbt.contains("master", NbtElement.INT_ARRAY_TYPE)) {
            masterPedestal = BlockPos.CODEC.parse(registryLookup.getOps(NbtOps.INSTANCE), nbt.get("master")).resultOrPartial(error -> LOGGER.error("Tried to load invalid block pos: '{}'", error)).orElse(null);
            index = nbt.getByte("index");
        } else {
            masterPedestal = null;
            index = -1;
        }

        if (nbt.contains("crafting")) {
            this.crafting = nbt.getShort("crafting");
        } else {
            this.crafting = -1;
        }

        this.locked = nbt.getBoolean("Locked");
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return this.createNbt(registryLookup);
    }
    //endregion
}
