package net.orandja.chocoflavor.tooltask;

import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Consumer;

public abstract class DoubleToolTask {

    @Getter private final String name;

    public DoubleToolTask(String name) {
        this.name = name;
    }

    public abstract void execute(World world, BlockPos pos, PlayerEntity player, BlockState state, ItemStack mainHand, ItemStack offHand, Consumer<BlockPos> consumer);

}
