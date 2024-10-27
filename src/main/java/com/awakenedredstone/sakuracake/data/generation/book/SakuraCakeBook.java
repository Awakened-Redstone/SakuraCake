package com.awakenedredstone.sakuracake.data.generation.book;

import com.awakenedredstone.sakuracake.SakuraCake;
import com.klikli_dev.modonomicon.api.datagen.BookProvider;
import net.minecraft.data.DataOutput;
import net.minecraft.registry.RegistryWrapper;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class SakuraCakeBook extends BookProvider {
    public SakuraCakeBook(DataOutput packOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registries, BiConsumer<String, String> defaultLang) {
        super(
          packOutput, registries, SakuraCake.MOD_ID,
          List.of(
            new SakuraCakeSubBook(defaultLang)
          )
        );
    }
}
