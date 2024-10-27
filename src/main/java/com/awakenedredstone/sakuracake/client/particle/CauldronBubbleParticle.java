package com.awakenedredstone.sakuracake.client.particle;

import com.awakenedredstone.sakuracake.registry.CherryBlocks;
import com.awakenedredstone.sakuracake.registry.CherryParticles;
import com.awakenedredstone.sakuracake.registry.block.CherryCauldronBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

@Environment(value=EnvType.CLIENT)
public class CauldronBubbleParticle extends SpriteBillboardParticle {
    private final double maxY;

    protected CauldronBubbleParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        super(world, x, y, z);
        Random random = new Random();
        this.setBoundingBoxSpacing(0.02f, 0.02f);
        this.maxY = y + .5625;
        this.scale *= 0.2f;
        this.maxAge = (int) (random.nextGaussian(50, 30));
    }

    @Override
    public void tick() {
        BlockState state = this.world.getBlockState(BlockPos.ofFloored(this.x, this.y, this.z));
        if (!state.isOf(CherryBlocks.CAULDRON) || !CherryCauldronBlock.hasWater(state)) {
            this.markDead();
        }

        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        if (this.y >= this.maxY || this.maxAge-- <= 0) {
            world.addParticle(CherryParticles.CAULDRON_BUBBLE_POP, x, y, z, velocityX, velocityY, velocityZ);
            this.markDead();
            return;
        }
        this.velocityY += 0.002;
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        this.velocityX *= 0.85f;
        this.velocityY *= 0.85f;
        this.velocityZ *= 0.85f;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    @Environment(value= EnvType.CLIENT)
    public record Factory(SpriteProvider spriteProvider) implements ParticleFactory<SimpleParticleType> {

        @Override
        public @NotNull Particle createParticle(SimpleParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            CauldronBubbleParticle particle = new CauldronBubbleParticle(world, x, y, z, velocityX, velocityY, velocityZ);
            particle.setSprite(this.spriteProvider);
            return particle;
        }
    }
}
