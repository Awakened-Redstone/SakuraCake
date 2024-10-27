package com.awakenedredstone.sakuracake.platform;

import com.awakenedredstone.sakuracake.SakuraCake;

//? if fabric {
import net.fabricmc.api.ModInitializer;

public final class SakuraCakeWrapper implements ModInitializer {
    @Override
    public void onInitialize() {
        SakuraCake.init();
    }
}
//?} elif neoforge {
/*//import com.awakenedredstone.sakuracake.client.platform.datagen.NeoModelDatagen;
import net.neoforged.fml.common.Mod;

@Mod(SakuraCake.MOD_ID)
public final class SakuraCakeWrapper {
    public SakuraCakeWrapper() {
        SakuraCake.init();
    }
}
*///?}
