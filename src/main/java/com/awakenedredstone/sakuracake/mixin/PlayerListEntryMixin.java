package com.awakenedredstone.sakuracake.mixin;

import com.awakenedredstone.sakuracake.SakuraCake;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@Mixin(PlayerListEntry.class)
public class PlayerListEntryMixin {
    private final Logger LOGGER = LogManager.getLogger("SakuraCake/PlayerListEntryMixin");
    @Shadow @Final private GameProfile profile;
    @Shadow @Final private Map<Type, Identifier> textures;
    @Shadow private boolean texturesLoaded;

    @Inject(method = "loadTextures", at = @At("HEAD"))
    private void loadTextures(CallbackInfo ci) {
        if (profile.getId().toString() != null) {
            if (!texturesLoaded) {
                registerCapes();
            }
            Identifier cape = getCape(profile);
            if (cape == null) return;
            this.textures.put(Type.CAPE, cape);
        }
    }

    private void registerCapes() {
        Path capesPath = FabricLoader.getInstance().getModContainer(SakuraCake.MOD_ID).get().getPath("data/sakuracake/special/capes.json");
        if (capesPath != null) {
            try (BufferedReader reader = Files.newBufferedReader(capesPath)) {
                JsonElement json = JsonParser.parseReader(new JsonReader(reader));
                if (json == null) return;
                JsonElement capeId = json.getAsJsonObject().get(profile.getId().toString());
                if (capeId != null) {
                    try (InputStream stream = Files.newInputStream(FabricLoader.getInstance().getModContainer(SakuraCake.MOD_ID).get().getPath(String.format("assets/sakuracake/textures/special/cape/%s.png", capeId.getAsString())))) {
                        MinecraftClient.getInstance().getTextureManager().registerTexture(new Identifier(SakuraCake.MOD_ID, String.format("%s", capeId.getAsString())), new NativeImageBackedTexture(NativeImage.read(stream)));
                    }
                }
            } catch (IOException ignored) {}
        }
    }

    private Identifier getCape(GameProfile profile) {
        Path capesPath = FabricLoader.getInstance().getModContainer(SakuraCake.MOD_ID).get().getPath("data/sakuracake/special/capes.json");
        if (capesPath != null) {
            try (BufferedReader reader = Files.newBufferedReader(capesPath)) {
                JsonElement json = JsonParser.parseReader(new JsonReader(reader));
                if (json == null) return null;
                JsonElement capeId = json.getAsJsonObject().get(profile.getId().toString());
                if (capeId != null) {
                    return new Identifier(SakuraCake.MOD_ID, String.format("%s", capeId.getAsString()));
                }
            } catch (IOException ignored) {}
        }
        return null;
    }

}
