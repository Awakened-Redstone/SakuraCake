package com.awakenedredstone.sakuracake.item.tools;

import com.awakenedredstone.sakuracake.SakuraCake;
import net.minecraft.item.Item;
import net.minecraft.util.Rarity;

public class SakuraToolBase extends Item {
    public SakuraToolBase(Settings settings) {
        super(settings.fireproof().group(SakuraCake.ITEM_GROUP).rarity(Rarity.EPIC));
    }
}
