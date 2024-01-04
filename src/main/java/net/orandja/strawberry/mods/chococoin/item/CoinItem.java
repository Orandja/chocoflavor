package net.orandja.strawberry.mods.chococoin.item;

import lombok.Getter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.ClickType;
import net.orandja.strawberry.intf.StrawberryItemHandler;

public class CoinItem extends Item implements StrawberryItemHandler {

    @Getter
    private final int value;
    private final int customDataModel;
    private final Item replacementItem;
    private final CoinItem childCoin;

    public CoinItem(Item replacementItem, int customDataModel, CoinItem childCoin, Settings settings) {
        super(settings);
        this.customDataModel = customDataModel;
        this.replacementItem = replacementItem;
        this.childCoin = childCoin;
        this.value = this.childCoin == null ? 1 : this.childCoin.value * 9;
    }
//
//    public List<ItemStack> getReminder(int value, ItemStack stack) {
//        if(value < emeraldValue) {
//
//        }
//    }

    @Override
    public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
        return super.onStackClicked(stack, slot, clickType, player);
//        if (clickType != ClickType.RIGHT) {
//            return false;
//        }
//        ItemStack itemStack = slot.getStack();
//        if (itemStack.isEmpty()) {
//            this.playRemoveOneSound(player);
//            BundleItem.removeFirstStack(stack).ifPresent(removedStack -> BundleItem.addToBundle(stack, slot.insertStack((ItemStack)removedStack)));
//        } else if (itemStack.getItem().canBeNested()) {
//            int i = (64 - BundleItem.getBundleOccupancy(stack)) / BundleItem.getItemOccupancy(itemStack);
//            int j = BundleItem.addToBundle(stack, slot.takeStackRange(itemStack.getCount(), i, player));
//            if (j > 0) {
//                this.playInsertSound(player);
//            }
//        }
//        return true;
    }

    public int getValue(ItemStack stack) {
        return stack.getItem() == this ? stack.getCount() * this.value : 0;
    }

    public int getMax() {
        return this.value * 64;
    }

    @Override
    public ItemStack transform(ItemStack sourceStack) {
        return transform(sourceStack, this.replacementItem, this.customDataModel);
    }
}
