package net.orandja.chocoflavor.mods.doubletools.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.MangroveRootsBlock;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.orandja.chocoflavor.mods.doubletools.DoubleTools;
import org.spongepowered.asm.mixin.Mixin;

@SuppressWarnings("unused")
@Mixin(value = {
        PillarBlock.class,
        MangroveRootsBlock.class
})
public abstract class PillarBlockMixin extends Block implements DoubleTools {

    public PillarBlockMixin(Settings settings) {
        super(settings);
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack stack) {
        useAxe(this, blockEntity, world, player, pos, state, stack, super::afterBreak);
    }
}
