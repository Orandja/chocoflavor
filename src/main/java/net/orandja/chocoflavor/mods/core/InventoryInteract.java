package net.orandja.chocoflavor.mods.core;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Pair;
import com.google.common.collect.Lists;
import net.orandja.chocoflavor.ChocoFlavor;

import java.util.List;
import java.util.function.Predicate;

public interface InventoryInteract {

    interface StackInteraction {
        void interact(ItemStack stack, PlayerEntity player);
    }

    List<Pair<Predicate<ItemStack>, StackInteraction>> onMiddleClick = Lists.newArrayList();

    default void checkMiddleClick(ItemStack cursorStack, SlotActionType actionType, PlayerEntity player) {
        if(!player.isCreative() && actionType.equals(SlotActionType.CLONE) && !cursorStack.isEmpty()) {
            onMiddleClick.stream().filter(it -> it.getLeft().test(cursorStack)).findFirst().ifPresent(it -> it.getRight().interact(cursorStack, player));
        }
    }
}
