package com.awakenedredstone.sakuracake.registry;

import com.awakenedredstone.sakuracake.SakuraCake;
import com.awakenedredstone.sakuracake.internal.registry.AutoRegistry;
import com.awakenedredstone.sakuracake.internal.registry.RegistryNamespace;
import com.awakenedredstone.sakuracake.particle.AbsorbColorTransitionParticleEffect;
import com.awakenedredstone.sakuracake.util.Cast;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DustColorTransitionParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

@RegistryNamespace(SakuraCake.MOD_ID)
public class CherryParticles implements AutoRegistry<ParticleType<?>> {
    public static final SimpleParticleType FOCUSED_AURA = FabricParticleTypes.simple(true);
    public static final SimpleParticleType CAULDRON_BUBBLE = FabricParticleTypes.simple(false);
    public static final SimpleParticleType CAULDRON_BUBBLE_POP = FabricParticleTypes.simple(false);
    public static final ParticleType<AbsorbColorTransitionParticleEffect> ABSORB = FabricParticleTypes.complex(false, type -> AbsorbColorTransitionParticleEffect.CODEC, type -> AbsorbColorTransitionParticleEffect.PACKET_CODEC);

    @Override
    public Registry<ParticleType<?>> registry() {
        return Registries.PARTICLE_TYPE;
    }

    @Override
    public Class<ParticleType<?>> fieldType() {
        return Cast.conform(ParticleType.class);
    }
}
