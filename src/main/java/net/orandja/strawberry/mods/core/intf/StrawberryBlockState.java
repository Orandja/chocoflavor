package net.orandja.strawberry.mods.core.intf;

import net.minecraft.block.BlockState;
import net.orandja.chocoflavor.ChocoFlavor;
import net.orandja.strawberry.mods.core.NoteBlockData;
import net.orandja.strawberry.mods.resourcepack.StrawberryResourcePackGenerator;

public interface StrawberryBlockState extends CustomRegistry {
    BlockState transform(BlockState blockState);

    void register();

    default void register(int id, String model) {
        ChocoFlavor.LOGGER.info(id + "; " + model);
        StrawberryResourcePackGenerator.noteblockModels.put(NoteBlockData.fromID(id), "minecraft:block/" + model);
    }
}
