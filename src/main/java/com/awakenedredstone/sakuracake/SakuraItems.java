package com.awakenedredstone.sakuracake;

import com.awakenedredstone.sakuracake.item.GiantPizzaItem;
import com.awakenedredstone.sakuracake.item.PhoenixElytraItem;
import com.awakenedredstone.sakuracake.item.magnet.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

public class SakuraItems {

    //Magnets
    public static final IronMagnet IRON_MAGNET = new IronMagnet(new FabricItemSettings().group(SakuraCake.ITEM_GROUP).rarity(Rarity.COMMON).maxDamage(96));
    public static final GoldenMagnet GOLDEN_MAGNET = new GoldenMagnet(new FabricItemSettings().group(SakuraCake.ITEM_GROUP).rarity(Rarity.COMMON).maxDamage(160));
    public static final DiamondMagnet DIAMOND_MAGNET = new DiamondMagnet(new FabricItemSettings().group(SakuraCake.ITEM_GROUP).rarity(Rarity.COMMON).maxDamage(224));
    public static final EmeraldMagnet EMERALD_MAGNET = new EmeraldMagnet(new FabricItemSettings().group(SakuraCake.ITEM_GROUP).rarity(Rarity.COMMON).maxDamage(256));
    public static final AncientMagnet ANCIENT_MAGNET = new AncientMagnet(new FabricItemSettings().group(SakuraCake.ITEM_GROUP).rarity(Rarity.UNCOMMON).maxDamage(512).fireproof());
    public static final NetheriteMagnet NETHERITE_MAGNET = new NetheriteMagnet(new FabricItemSettings().group(SakuraCake.ITEM_GROUP).rarity(Rarity.COMMON).maxDamage(712).fireproof());
    public static final SakuraMagnet SAKURA_MAGNET = new SakuraMagnet(new FabricItemSettings().group(SakuraCake.ITEM_GROUP).rarity(Rarity.EPIC).maxDamage(8008).fireproof());
    //Others
    public static final GiantPizzaItem GIANT_PIZZA = new GiantPizzaItem(new FabricItemSettings().group(SakuraCake.ITEM_GROUP).rarity(Rarity.UNCOMMON));
    public static final PhoenixElytraItem WINGS_OF_ETERNAL_FLAMES = new PhoenixElytraItem(new FabricItemSettings().group(SakuraCake.ITEM_GROUP).rarity(Rarity.EPIC).maxDamage(432).fireproof());

    public static void registerItems() {
        Registry.register(Registry.ITEM, new Identifier(SakuraCake.MOD_ID, "iron_sakura_magnet"), IRON_MAGNET);
        Registry.register(Registry.ITEM, new Identifier(SakuraCake.MOD_ID, "golden_sakura_magnet"), GOLDEN_MAGNET);
        Registry.register(Registry.ITEM, new Identifier(SakuraCake.MOD_ID, "diamond_sakura_magnet"), DIAMOND_MAGNET);
        Registry.register(Registry.ITEM, new Identifier(SakuraCake.MOD_ID, "emerald_sakura_magnet"), EMERALD_MAGNET);
        Registry.register(Registry.ITEM, new Identifier(SakuraCake.MOD_ID, "ancient_sakura_magnet"), ANCIENT_MAGNET);
        Registry.register(Registry.ITEM, new Identifier(SakuraCake.MOD_ID, "netherite_sakura_magnet"), NETHERITE_MAGNET);
        Registry.register(Registry.ITEM, new Identifier(SakuraCake.MOD_ID, "sakura_magnet"), SAKURA_MAGNET);
        Registry.register(Registry.ITEM, new Identifier(SakuraCake.MOD_ID, "giant_pizza"), GIANT_PIZZA);
        Registry.register(Registry.ITEM, new Identifier(SakuraCake.MOD_ID, "wings_of_eternal_flames"), WINGS_OF_ETERNAL_FLAMES);
    }
}
