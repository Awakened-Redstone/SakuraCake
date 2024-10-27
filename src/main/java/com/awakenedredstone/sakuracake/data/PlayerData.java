package com.awakenedredstone.sakuracake.data;

import com.awakenedredstone.sakuracake.SakuraCake;
import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

@SuppressWarnings("UnstableApiUsage")
public class PlayerData {
    public static final AttachmentType<Integer> CAULDRON_SLOT = AttachmentRegistry.<Integer>builder()
      .copyOnDeath()
      .initializer(() -> 0)
      .persistent(Codec.INT)
      .buildAndRegister(SakuraCake.id("cauldron_slot"));
}
