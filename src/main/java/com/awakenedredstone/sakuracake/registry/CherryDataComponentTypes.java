package com.awakenedredstone.sakuracake.registry;

import com.awakenedredstone.sakuracake.SakuraCake;
import com.awakenedredstone.sakuracake.internal.registry.AutoRegistry;
import com.awakenedredstone.sakuracake.internal.registry.RegistryNamespace;
import com.awakenedredstone.sakuracake.util.Cast;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Unit;

@RegistryNamespace(SakuraCake.MOD_ID)
public class CherryDataComponentTypes implements AutoRegistry<ComponentType<?>> {
    //public static final ComponentType<Unit> CAULDRON_DROP = ComponentType.<Unit>builder().codec(Unit.CODEC).packetCodec(PacketCodec.unit(Unit.INSTANCE)).build();

    @Override
    public Registry<ComponentType<?>> registry() {
        return Registries.DATA_COMPONENT_TYPE;
    }

    @Override
    public Class<ComponentType<?>> fieldType() {
        return Cast.conform(ComponentType.class);
    }
}
