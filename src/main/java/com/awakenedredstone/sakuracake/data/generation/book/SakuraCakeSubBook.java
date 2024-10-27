package com.awakenedredstone.sakuracake.data.generation.book;

import com.awakenedredstone.sakuracake.SakuraCake;
import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookModel;
import com.klikli_dev.modonomicon.datagen.book.demo.ConditionalCategory;
import com.klikli_dev.modonomicon.datagen.book.demo.FeaturesCategory;
import com.klikli_dev.modonomicon.datagen.book.demo.FormattingCategory;
import com.klikli_dev.modonomicon.datagen.book.demo.IndexModeCategory;
import com.klikli_dev.modonomicon.datagen.book.demo.features.ConditionRootEntry;

import java.util.function.BiConsumer;

public class SakuraCakeSubBook extends SingleBookSubProvider {
        public SakuraCakeSubBook(BiConsumer<String, String> defaultLang) {
            super("handbook", SakuraCake.MOD_ID, defaultLang);
        }

        @Override
        protected BookModel additionalSetup(BookModel book) {
            return super.additionalSetup(book)
              .withGenerateBookItem(false)
              .withCustomBookItem(SakuraCake.id(bookId));
        }

        @Override
        protected void registerDefaultMacros() {}

        @Override
        protected void generateCategories() {
            this.add(new FeaturesCategory(this).generate());
            this.add(new FormattingCategory(this).generate());

            this.add(new ConditionalCategory(this).generate())
              .withCondition(this.condition().entryRead(this.modLoc(FeaturesCategory.ID, ConditionRootEntry.ID)));

            this.add(new IndexModeCategory(this).generate());
        }

        @Override
        protected String bookName() {
            return "handbook";
        }

        @Override
        protected String bookTooltip() {
            return "book.sakuracake.handbook.tooltip";
        }
    }
