//package net.orandja.chocoflavor.mods.core;
//
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.item.ItemStack;
//import net.minecraft.screen.slot.SlotActionType;
//import net.minecraft.util.Pair;
//import com.google.common.collect.Lists;
//
//import java.util.List;
//import java.util.function.BiConsumer;
//import java.util.function.Predicate;
//
//public interface InventoryInteract {
//
//    List<Pair<Predicate<ItemStack>, BiConsumer<ItemStack, PlayerEntity>>> onMiddleClick = Lists.newArrayList();
//
//    default void checkMiddleClick(ItemStack cursorStack, SlotActionType actionType, PlayerEntity player) {
//        if(!player.isCreative() && actionType.equals(SlotActionType.CLONE) && !cursorStack.isEmpty()) {
//            onMiddleClick.stream().filter(it -> it.getLeft().test(cursorStack)).findFirst().ifPresent(it -> it.getRight().accept(cursorStack, player));
//        }
//    }
//}
