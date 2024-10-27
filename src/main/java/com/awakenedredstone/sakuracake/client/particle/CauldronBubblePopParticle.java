package com.awakenedredstone.sakuracake.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;

@Environment(EnvType.CLIENT)
public class CauldronBubblePopParticle extends SpriteBillboardParticle {
    private final SpriteProvider spriteProvider;

    public CauldronBubblePopParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
        super(world, x, y, z);
        this.spriteProvider = spriteProvider;
        this.maxAge = 4;
        this.scale *= 0.4f;
        this.gravityStrength = 0.008F;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;
        this.setSpriteForAge(spriteProvider);
    }

    @Override
    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        if (this.age++ >= this.maxAge) {
            this.markDead();
        } else {
            this.velocityY = this.velocityY - (double)this.gravityStrength;
            this.move(this.velocityX, this.velocityY, this.velocityZ);
            this.setSpriteForAge(this.spriteProvider);
        }
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType particleType, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new CauldronBubblePopParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.spriteProvider);
        }
    }
}
