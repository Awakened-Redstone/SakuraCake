package com.awakenedredstone.sakuracake.client.particle;

import com.awakenedredstone.sakuracake.client.render.CherryParticleTextureSheets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ShaderParticle extends Particle {
    protected ShaderParticle(ClientWorld world, double x, double y, double z) {
        super(world, x, y, z);
        maxAge = 0;
    }

    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        Vec3d cameraPos = camera.getPos();
        double d = cameraPos.x - this.x;
        double e = cameraPos.y - this.y;
        double f = cameraPos.z - this.z;
        double g = Math.sqrt(d * d + f * f);

        Quaternionf quaternionf = new Quaternionf();
        quaternionf.rotateY((float) (-MathHelper.atan2(f, d) + Math.PI / 2));
        quaternionf.rotateX((float) -MathHelper.atan2(e, g));

        this.method_60373(vertexConsumer, camera, quaternionf, tickDelta);
    }

    protected void method_60373(VertexConsumer vertexConsumer, Camera camera, Quaternionf quaternionf, float tickDelta) {
        Vec3d cameraPos = camera.getPos();
        float x = (float) (MathHelper.lerp(tickDelta, this.prevPosX, this.x) - cameraPos.getX());
        float y = (float) (MathHelper.lerp(tickDelta, this.prevPosY, this.y) - cameraPos.getY());
        float z = (float) (MathHelper.lerp(tickDelta, this.prevPosZ, this.z) - cameraPos.getZ());

        this.method_60374(vertexConsumer, quaternionf, x, y, z, tickDelta);
    }

    protected void method_60374(VertexConsumer vertexConsumer, Quaternionf quaternionf, float x, float y, float z, float tickDelta) {
        float scale = .5f;
        float u1 = 0;
        float u2 = 1;
        float v1 = 0;
        float v2 = 1;
        int v = this.getBrightness(tickDelta);
        this.method_60375(vertexConsumer, quaternionf, x, y, z, 1.0f, -1.0f, scale, u2, v2, v);
        this.method_60375(vertexConsumer, quaternionf, x, y, z, 1.0f, 1.0f, scale, u2, v1, v);
        this.method_60375(vertexConsumer, quaternionf, x, y, z, -1.0f, 1.0f, scale, u1, v1, v);
        this.method_60375(vertexConsumer, quaternionf, x, y, z, -1.0f, -1.0f, scale, u1, v2, v);
    }

    private void method_60375(VertexConsumer vertexConsumer, Quaternionf quaternionf, float x, float y, float z, float u1, float v1, float scale, float u2, float v2, int lightUV) {
        Vector3f vector3f = new Vector3f(u1, v1, 0.0f).rotate(quaternionf).mul(scale).add(x, y, z);
        vertexConsumer.vertex(vector3f.x(), vector3f.y(), vector3f.z()).texture(u2, v2).color(this.red, this.green, this.blue, this.alpha).light(lightUV);
    }

    @Override
    public ParticleTextureSheet getType() {
        return CherryParticleTextureSheets.SHADER_SHEET;
    }

    @Environment(value = EnvType.CLIENT)
    public static class Factory implements ParticleFactory<SimpleParticleType> {
        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            ShaderParticle particle = new ShaderParticle(clientWorld, d, e, f);
            particle.setColor(0, 1, 1);
            return particle;
        }
    }
}
