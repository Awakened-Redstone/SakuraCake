package com.awakenedredstone.sakuracake.client.particle;

import com.awakenedredstone.sakuracake.particle.AbsorbColorTransitionParticleEffect;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.AbstractDustParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;

@Environment(EnvType.CLIENT)
public class AbsorbParticle extends AbstractDustParticle<AbsorbColorTransitionParticleEffect> {
    protected AbsorbParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, AbsorbColorTransitionParticleEffect parameters, SpriteProvider spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ, parameters, spriteProvider);
        this.red = parameters.getColor().x();
        this.green = parameters.getColor().y();
        this.blue = parameters.getColor().z();
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<AbsorbColorTransitionParticleEffect> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(AbsorbColorTransitionParticleEffect effect, ClientWorld clientWorld, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new AbsorbParticle(clientWorld, x, y, z, velocityX, velocityY, velocityZ, effect, this.spriteProvider);
        }
    }
}
