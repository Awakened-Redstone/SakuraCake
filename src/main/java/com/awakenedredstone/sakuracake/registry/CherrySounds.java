package com.awakenedredstone.sakuracake.registry;

import com.awakenedredstone.sakuracake.SakuraCake;
import com.awakenedredstone.sakuracake.internal.registry.AutoRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;

public class CherrySounds implements AutoRegistry<SoundEvent> {
    public static final SoundEvent CAULDRON_CRAFT = SoundEvent.of(SakuraCake.id("cauldron_craft"));

    @Override
    public Registry<SoundEvent> registry() {
        return Registries.SOUND_EVENT;
    }

    @Override
    public Class<SoundEvent> fieldType() {
        return SoundEvent.class;
    }
}
