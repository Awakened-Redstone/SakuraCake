package com.awakenedredstone.sakuracake.particle;

import com.awakenedredstone.sakuracake.registry.CherryParticles;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.particle.AbstractDustParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

public class AbsorbColorTransitionParticleEffect extends AbstractDustParticleEffect {
    public static final Vector3f BLUE = Vec3d.unpackRgb(0xf4a6c9).toVector3f();
    public static final Vector3f PINK = Vec3d.unpackRgb(0x90dff9).toVector3f();
    public static final MapCodec<AbsorbColorTransitionParticleEffect> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
          Codecs.VECTOR_3F.fieldOf("color").forGetter(effect -> effect.color),
          SCALE_CODEC.fieldOf("scale").forGetter(AbstractDustParticleEffect::getScale)
        )
        .apply(instance, AbsorbColorTransitionParticleEffect::new)
    );
    public static final PacketCodec<RegistryByteBuf, AbsorbColorTransitionParticleEffect> PACKET_CODEC = PacketCodec.tuple(
      PacketCodecs.VECTOR3F,
      effect -> effect.color,
      PacketCodecs.FLOAT,
      AbstractDustParticleEffect::getScale,
      AbsorbColorTransitionParticleEffect::new
    );
    private final Vector3f color;

    public AbsorbColorTransitionParticleEffect(Vector3f color, float scale) {
        super(scale);
        this.color = color;
    }

    public Vector3f getColor() {
        return this.color;
    }

    @Override
    public ParticleType<AbsorbColorTransitionParticleEffect> getType() {
        return CherryParticles.ABSORB;
    }
}
