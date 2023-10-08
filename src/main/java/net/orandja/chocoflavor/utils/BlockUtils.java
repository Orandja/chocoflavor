package net.orandja.chocoflavor.utils;

import net.minecraft.block.*;
import net.minecraft.registry.tag.BlockTags;
import net.orandja.chocoflavor.ChocoFlavor;
import net.orandja.chocoflavor.mods.doubletools.mixin.ChocoFlavorInit;

public abstract class BlockUtils {

    public static boolean isOre(Block block) {
        return block instanceof ExperienceDroppingBlock || block instanceof RedstoneOreBlock;
    }

    public static boolean isOreAndTheSame(Block block1, Block block2) {
        return isOre(block1) && block2 == block1;
    }

    public static boolean isOre(BlockState state) {
        return isOre(state.getBlock());
    }

    public static boolean isOreAndTheSame(BlockState state1, BlockState state2) {
        return isOreAndTheSame(state1.getBlock(), state2.getBlock());
    }

    public static boolean isOreAndTheSame(BlockState state, Block block) {
        return isOreAndTheSame(state.getBlock(), block);
    }

    public static boolean isWood(BlockState state) {
        return state.isIn(BlockTags.LOGS) || state.getBlock() == Blocks.CRIMSON_HYPHAE || state.getBlock() == Blocks.WARPED_HYPHAE;
    }

    public static boolean isWoodAndTheSame(BlockState state1, BlockState state2) {
        return isWood(state1) && state1.getBlock() == state2.getBlock();
    }

}
