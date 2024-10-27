package com.awakenedredstone.sakuracake.registry;

import com.awakenedredstone.sakuracake.SakuraCake;
import com.awakenedredstone.sakuracake.internal.registry.AutoRegistry;
import com.awakenedredstone.sakuracake.internal.registry.RegistryNamespace;
import com.awakenedredstone.sakuracake.registry.entity.FloatingItemEntity;
import com.awakenedredstone.sakuracake.util.Cast;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

@RegistryNamespace(SakuraCake.MOD_ID)
public class CherryEntityTypes implements AutoRegistry<EntityType<?>> {
    public static final EntityType<FloatingItemEntity> FLOATING_ITEM = EntityType.Builder.<FloatingItemEntity>create(FloatingItemEntity::new, SpawnGroup.MISC)
      .dimensions(0.25f, 0.25f).eyeHeight(0.2125f).maxTrackingRange(6).trackingTickInterval(20)
      .build("floating_item");

    @Override
    public Registry<EntityType<?>> registry() {
        return Registries.ENTITY_TYPE;
    }

    @Override
    public Class<EntityType<?>> fieldType() {
        return Cast.conform(EntityType.class);
    }
}
