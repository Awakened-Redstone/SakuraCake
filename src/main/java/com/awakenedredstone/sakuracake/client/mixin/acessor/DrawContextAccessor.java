package com.awakenedredstone.sakuracake.client.mixin.acessor;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.GuiAtlasManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DrawContext.class)
public interface DrawContextAccessor {
    @Accessor GuiAtlasManager getGuiAtlasManager();
    @Invoker void callTryDraw();
}
