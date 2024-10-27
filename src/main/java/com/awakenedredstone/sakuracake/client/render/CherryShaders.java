package com.awakenedredstone.sakuracake.client.render;

import com.awakenedredstone.sakuracake.SakuraCake;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.lang.reflect.Field;

public class CherryShaders {
    public static final Shader PARTICLES = new Shader(VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);

    public static void register(CoreShaderRegistrationCallback.RegistrationContext context) throws IOException {
        for (Field field : CherryShaders.class.getDeclaredFields()) {
            String name = field.getName().toLowerCase();
            Identifier id = SakuraCake.id(name);
            try {
                Object fieldData = field.get(null);
                if (!(fieldData instanceof Shader shader)) continue;

                context.register(id, shader.vertexFormat, program -> shader.program = program);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to register shaders due to field access", e);
            }
        }
    }

    public static class Shader {
        final VertexFormat vertexFormat;
        ShaderProgram program;

        private Shader(VertexFormat vertexFormat) {
            this.vertexFormat = vertexFormat;
        }

        public ShaderProgram shaders() {
            return program;
        }
    }
}
