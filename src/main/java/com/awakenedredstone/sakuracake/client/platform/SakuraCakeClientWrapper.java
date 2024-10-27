package com.awakenedredstone.sakuracake.client.platform;

import com.awakenedredstone.sakuracake.client.SakuraCakeClient;

//? if fabric {
/*import net.fabricmc.api.ClientModInitializer;

public final class SakuraCakeClientWrapper implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SakuraCakeClient.init();
    }
}
*///?} elif neoforge {
import com.awakenedredstone.sakuracake.SakuraCake;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = SakuraCake.MOD_ID, dist = Dist.CLIENT)
public final class SakuraCakeClientWrapper {
    public SakuraCakeClientWrapper() {
        SakuraCakeClient.init();
    }
}
//?}
