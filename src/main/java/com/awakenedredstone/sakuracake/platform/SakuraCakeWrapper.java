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
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataOutput;
import net.minecraft.registry.RegistryWrapper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@Mod(SakuraCake.MOD_ID)
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = SakuraCake.MOD_ID)
public final class SakuraCakeWrapper {
    public SakuraCakeWrapper() {
        SakuraCake.init();
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        // Data generators may require some of these as constructor parameters.
        // See below for more details on each of these.
        DataGenerator generator = event.getGenerator();
        DataOutput output = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<RegistryWrapper.WrapperLookup> lookupProvider = event.getLookupProvider();

        // Register the provider.
        /^generator.addProvider(
          // A boolean that determines whether the data should actually be generated.
          // The event provides methods that determine this:
          // event.includeClient(), event.includeServer(),
          // event.includeDev() and event.includeReports().
          // Since recipes are server data, we only run them in a server datagen.
          event.includeServer(),
          // Our provider.
          new NeoModelDatagen(output, existingFileHelper)
        );^/
        // Other data providers here.
    }
}
*///?}
