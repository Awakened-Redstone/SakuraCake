package com.awakenedredstone.sakuracake;

import com.awakenedredstone.sakuracake.item.*;
import com.awakenedredstone.sakuracake.item.food.GiantPizzaItem;
import com.awakenedredstone.sakuracake.item.food.GoldenBeetrootItem;
import com.awakenedredstone.sakuracake.item.magnet.*;
import com.awakenedredstone.sakuracake.item.weapons.sword.SakuraSword;
import io.wispforest.owo.registration.reflect.ItemRegistryContainer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

public class SakuraItems implements ItemRegistryContainer {

    //Magnets
    public static final IronMagnet IRON_MAGNET = new IronMagnet(new FabricItemSettings().group(SakuraCake.ITEM_GROUP).rarity(Rarity.COMMON).maxDamage(96));
    public static final GoldenMagnet GOLDEN_MAGNET = new GoldenMagnet(new FabricItemSettings().group(SakuraCake.ITEM_GROUP).rarity(Rarity.COMMON).maxDamage(160));
    public static final DiamondMagnet DIAMOND_MAGNET = new DiamondMagnet(new FabricItemSettings().group(SakuraCake.ITEM_GROUP).rarity(Rarity.COMMON).maxDamage(224));
    public static final EmeraldMagnet EMERALD_MAGNET = new EmeraldMagnet(new FabricItemSettings().group(SakuraCake.ITEM_GROUP).rarity(Rarity.COMMON).maxDamage(256));
    public static final AncientMagnet ANCIENT_MAGNET = new AncientMagnet(new FabricItemSettings().group(SakuraCake.ITEM_GROUP).rarity(Rarity.UNCOMMON).maxDamage(512).fireproof());
    public static final NetheriteMagnet NETHERITE_MAGNET = new NetheriteMagnet(new FabricItemSettings().group(SakuraCake.ITEM_GROUP).rarity(Rarity.RARE).maxDamage(712).fireproof());
    public static final SakuraMagnet SAKURA_MAGNET = new SakuraMagnet(new FabricItemSettings().group(SakuraCake.ITEM_GROUP).rarity(Rarity.EPIC).maxDamage(8008).fireproof());
    //Others
    public static final GiantPizzaItem GIANT_PIZZA = new GiantPizzaItem(new FabricItemSettings().group(SakuraCake.ITEM_GROUP).rarity(Rarity.UNCOMMON));
    public static final PhoenixElytraItem WINGS_OF_ETERNAL_FLAMES = new PhoenixElytraItem(new FabricItemSettings().group(SakuraCake.ITEM_GROUP).rarity(Rarity.EPIC).maxDamage(432).fireproof());
    public static final CrimsonStaff CRIMSON_STAFF = new CrimsonStaff(new FabricItemSettings().group(SakuraCake.ITEM_GROUP).rarity(Rarity.EPIC).maxCount(1).fireproof());
    public static final QuartzShard QUARTZ_SHARD = new QuartzShard(new FabricItemSettings().group(SakuraCake.ITEM_GROUP));
    public static final SakuraSword SAKURA_SWORD = new SakuraSword(new FabricItemSettings().maxDamage(2048));
    public static final GoldenBeetrootItem GOLDEN_BEETROOT = new GoldenBeetrootItem(new FabricItemSettings().group(SakuraCake.ITEM_GROUP));
}
