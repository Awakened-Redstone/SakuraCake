package com.awakenedredstone.sakuracake.client.render;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public class CherryHudRenderer {
    public static void render(DrawContext drawContext, RenderTickCounter tickCounter) {
        CauldronHudRenderer.render(drawContext, tickCounter);
    }
}
