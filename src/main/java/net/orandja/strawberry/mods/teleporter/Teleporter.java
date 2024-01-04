package net.orandja.strawberry.mods.teleporter;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

public class Teleporter {

    public static Block TELEPORTER;
    public static Item TELEPORTER_ITEM;

    public static void beforeLaunch() {
//        TELEPORTER = Blocks.register("teleporter", new MufflerBlock(26, "teleporter", it -> {
//            it.mapColor(MapColor.STONE_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(3.5f);
//        }));
//        TELEPORTER_ITEM = Items.register(new SimpleBlockItem(TELEPORTER, 26, new Item.Settings()));
    }
}
