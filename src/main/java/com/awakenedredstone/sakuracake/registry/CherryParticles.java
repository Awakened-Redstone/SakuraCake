package com.awakenedredstone.sakuracake.registry;

import com.awakenedredstone.sakuracake.SakuraCake;
import com.awakenedredstone.sakuracake.internal.registry.AutoRegistry;
import com.awakenedredstone.sakuracake.internal.registry.RegistryNamespace;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

@RegistryNamespace(SakuraCake.MOD_ID)
public class CherryParticles implements AutoRegistry<ParticleType<?>> {
    public static final SimpleParticleType SPARKLE_PARTICLE = FabricParticleTypes.simple(true);

    @Override
    public Registry<ParticleType<?>> registry() {
        return Registries.PARTICLE_TYPE;
    }

    @Override
    public Class<ParticleType<?>> fieldType() {
        return AutoRegistry.conform(ParticleType.class);
    }
}
