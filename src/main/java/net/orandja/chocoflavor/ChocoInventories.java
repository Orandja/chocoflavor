package net.orandja.chocoflavor;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Pair;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class ChocoInventories {
    private ChocoInventories() {}

    private static List<Pair<Predicate<ItemStack>, BiConsumer<ItemStack, PlayerEntity>>> onMiddleClick = Lists.newArrayList();
    public static void listenForMiddleClick(Predicate<ItemStack> predicate, BiConsumer<ItemStack, PlayerEntity> consumer) {
        onMiddleClick.add(new Pair<>(predicate, consumer));
    }

    public interface MiddleClickHandler {
        default void checkMiddleClick(ItemStack cursorStack, SlotActionType actionType, PlayerEntity player) {
            if(!player.isCreative() && actionType.equals(SlotActionType.CLONE) && !cursorStack.isEmpty()) {
                for (Pair<Predicate<ItemStack>, BiConsumer<ItemStack, PlayerEntity>> predicateBiConsumerPair : onMiddleClick) {
                    if(predicateBiConsumerPair.getLeft().test(cursorStack)) {
                        predicateBiConsumerPair.getRight().accept(cursorStack, player);
                    }
                }
            }
        }
    }
}
