package com.awakenedredstone.sakuracake.registry.entity;

import com.awakenedredstone.sakuracake.client.mixin.acessor.ItemEntityAccessor;
import com.awakenedredstone.sakuracake.registry.CherryEntityTypes;
import com.awakenedredstone.sakuracake.util.CherryMath;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class FloatingItemEntity extends ItemEntity {
    private double slowdown = 0.001;

    public FloatingItemEntity(EntityType<? extends ItemEntity> entityType, World world) {
        super(entityType, world);
        setNoGravity(true);
    }

    public FloatingItemEntity(World world) {
        this(CherryEntityTypes.FLOATING_ITEM, world);
    }

    public FloatingItemEntity(World world, double x, double y, double z, ItemStack stack) {
        super(world, x, y, z, stack, 0, 0.2, 0);
        setNoGravity(true);
    }

    @Override
    protected boolean canMerge() {
        return super.canMerge() && ((ItemEntityAccessor) this).getPickupDelay() <= 0;
    }

    @Override
    public void tick() {
        super.tick();

        this.setVelocity(getVelocity().multiply(1 - (slowdown = CherryMath.easeOutExpo(slowdown)) / 2));
        this.scheduleVelocityUpdate();
    }
}
