package net.orandja.chocoflavor.tooltask;

import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.orandja.chocoflavor.utils.PlayerUtils;

import java.util.function.Consumer;

public abstract class DoubleToolTask {

    @Getter private final String name;

    public DoubleToolTask(String name) {
        this.name = name;
    }

    public abstract boolean execute(World world, BlockPos pos, PlayerEntity player, BlockState state, ItemStack mainHand, ItemStack offHand, Consumer<BlockPos> consumer);

    protected static void damageTools(PlayerEntity player, BlockState state) {
        player.getMainHandStack().damage(1, player, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        player.getOffHandStack().damage(1, player, e -> e.sendEquipmentBreakStatus(EquipmentSlot.OFFHAND));

        player.incrementStat(Stats.MINED.getOrCreateStat(state.getBlock()));
        player.addExhaustion(0.005f);
    }

    protected static void destroyBlock(PlayerEntity player, BlockState state, BlockPos pos) {
        damageTools(player, state);
        PlayerUtils.tryBreakBlock(player, pos);
    }
}
