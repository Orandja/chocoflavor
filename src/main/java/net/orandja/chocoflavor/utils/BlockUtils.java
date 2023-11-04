package net.orandja.chocoflavor.utils;

import net.minecraft.block.*;
import net.minecraft.registry.tag.BlockTags;

import java.util.Objects;

public abstract class BlockUtils {

    public static boolean isOre(Block block) {
        return block instanceof ExperienceDroppingBlock || block instanceof RedstoneOreBlock;
    }

    public static boolean isOreAndTheSame(Block block1, Block block2) {
        return isOre(block1) && Objects.equals(block2, block1);
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
        return state.isIn(BlockTags.LOGS) || Utils.anyEquals(state.getBlock(), (Object[]) ADDITIONNAL_LOG_BLOCKS);
    }

    public static boolean isLeaves(BlockState state) {
        return (state.isIn(BlockTags.LEAVES) && !state.get(LeavesBlock.PERSISTENT)) || state.isOf(Blocks.VINE);
    }

    public static boolean areSameLeaves(BlockState state, Block leaves) {
        return state.isOf(leaves);
    }

    private final static Block[] ADDITIONNAL_LOG_BLOCKS = new Block[] { Blocks.CRIMSON_HYPHAE, Blocks.WARPED_HYPHAE, Blocks.MANGROVE_ROOTS, Blocks.MANGROVE_LOG };
    private final static Block[] MANGROVE_LOG_BLOCKS = new Block[] { Blocks.MANGROVE_ROOTS, Blocks.MANGROVE_LOG };
    public static boolean areSameWoods(BlockState state, Block baseWood) {
        if(Utils.anyEquals(baseWood, (Object[]) MANGROVE_LOG_BLOCKS)) {
            return Utils.anyEquals(state.getBlock(), (Object[]) MANGROVE_LOG_BLOCKS);
        }
        return state.isIn(BlockTags.LOGS) || state.getBlock() == Blocks.CRIMSON_HYPHAE || state.getBlock() == Blocks.WARPED_HYPHAE;
    }

    public static boolean isWoodAndTheSame(BlockState state1, BlockState state2) {
        return isWood(state1) && Objects.equals(state1.getBlock(), state2.getBlock());
    }

}
