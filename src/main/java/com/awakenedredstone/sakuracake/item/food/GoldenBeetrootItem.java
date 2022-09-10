package com.awakenedredstone.sakuracake.item.food;

import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;

public class GoldenBeetrootItem extends Item {
    public GoldenBeetrootItem(Settings settings) {
        super(settings.food(new FoodComponent.Builder().hunger(8).saturationModifier(1.1f).build()));
    }
}
