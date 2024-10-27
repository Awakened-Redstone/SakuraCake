package com.awakenedredstone.sakuracake.client.render;

import net.minecraft.client.render.*;

import static net.minecraft.client.render.RenderPhase.CUTOUT_MIPPED_PROGRAM;

public class CherryRenderLayers {
    //public static final RenderPhase.ShaderProgram CUTOUT_MIPPED_PROGRAM = new RenderPhase.ShaderProgram(CherryShaders.PARTICLES::shaders);
    public static final RenderLayer CUTOUT_MIPPED_TRANSLUCENT = RenderLayer.of(
      "cutout_mipped_translucent",
      VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
      VertexFormat.DrawMode.QUADS,
      0x400000,
      true, true,
      RenderLayer.of(CUTOUT_MIPPED_PROGRAM)
    );
}
