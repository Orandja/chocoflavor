package net.orandja.strawberry.mods.muffler;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.Instrument;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.orandja.chocoflavor.utils.BlockZone;
import net.orandja.strawberry.mods.core.item.SimpleBlockItem;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public interface Muffler {

    AtomicReference<Block> MUFFLER_BLOCK = new AtomicReference<>();
    AtomicReference<Item> MUFFLER_ITEM = new AtomicReference<>();
    static void beforeLaunch() {
        MUFFLER_BLOCK.set(Blocks.register("muffler", new MufflerBlock(25, "muffler", it -> {
            it.mapColor(MapColor.STONE_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(3.5f);
        })));
        MUFFLER_ITEM.set(Items.register(new SimpleBlockItem(MUFFLER_BLOCK.get(), 25, new Item.Settings())));
    }

    BlockZone getMuffledZone(BlockPos pos);
    Map<BlockPos, BlockZone> getMuffledZones();

    default boolean isMuffled(BlockPos pos) {
        return isMuffled(pos.getX(), pos.getY(), pos.getZ());
    }

    default boolean isMuffled(double x, double y, double z) {
        for (BlockZone zone : getMuffledZones().values()) {
            if(zone.contains(x, y ,z)) {
                return true;
            }
        }

        return false;
    }
}
