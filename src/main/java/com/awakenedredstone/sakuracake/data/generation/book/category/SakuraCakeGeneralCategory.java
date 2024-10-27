package com.awakenedredstone.sakuracake.data.generation.book.category;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.ModonomiconProviderBase;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;

public class SakuraCakeGeneralCategory extends CategoryProvider {
    public SakuraCakeGeneralCategory(ModonomiconProviderBase parent) {
        super(parent);
    }

    @Override
    protected String[] generateEntryMap() {
        return new String[] {
          "__________________________________",
          "__________________________________",
          "__________________________________",
          "__________________________________",
          "________________c_m_______________",
          "___________________w______________",
          "__________________________________",
          "________________b__o_e____________",
          "__________________x_h_____________",
          "________________d_________________",
          "__________________________________",
          "__________________t_______________",
          "__________________________________",
          "__________________________________",
          "__________________________________"
        };
    }

    @Override
    protected void generateEntries() {

    }

    @Override
    protected String categoryName() {
        return "";
    }

    @Override
    protected BookIconModel categoryIcon() {
        return null;
    }

    @Override
    public String categoryId() {
        return "";
    }
}
