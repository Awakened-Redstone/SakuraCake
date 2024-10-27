package com.awakenedredstone.sakuracake.data.generation;

import com.awakenedredstone.sakuracake.data.generation.book.SakuraCakeBook;
import com.awakenedredstone.sakuracake.registry.CherryBlocks;
import com.klikli_dev.modonomicon.api.datagen.LanguageProviderCache;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

import java.util.concurrent.CompletableFuture;

public final class SakuraCakeDataGen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(BlockTagGen::new);
        pack.addProvider(SakuraCakeBlockLootTables::new);

        /*var enUsCache = new LanguageProviderCache("en_us");
        pack.addProvider((output, registriesFuture) -> new SakuraCakeBook(output, registriesFuture, enUsCache));*/
    }

    private static class BlockTagGen extends FabricTagProvider<Block> {
        public BlockTagGen(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, RegistryKeys.BLOCK, registriesFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup lookup) {
            ((FabricTagProvider<Block>.FabricTagBuilder) getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE))
              .add(CherryBlocks.CAULDRON)
              .add(CherryBlocks.PEDESTAL);

            ((FabricTagProvider<Block>.FabricTagBuilder) getOrCreateTagBuilder(BlockTags.AXE_MINEABLE))
              .add(CherryBlocks.PEDESTAL);
        }
    }

    private static class SakuraCakeBlockLootTables extends FabricBlockLootTableProvider {
        public SakuraCakeBlockLootTables(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(dataOutput, registriesFuture);
        }

        @Override
        public void generate() {
            addDrop(CherryBlocks.CAULDRON, drops(CherryBlocks.CAULDRON));
            addDrop(CherryBlocks.PEDESTAL, drops(CherryBlocks.PEDESTAL));
        }
    }
}
