package net.orandja.strawberry.mods.core.screen;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class ProgressRedirectSlot extends RedirectSlot {

    public interface ProgressGetter {
        int get();
    }

    public final ProgressGetter progressGetter;

    public ProgressRedirectSlot(Inventory inventory, int fakeIndex, int realIndex, ProgressGetter progressGetter) {
        super(inventory, fakeIndex, realIndex);
        this.progressGetter = progressGetter;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return false;
    }


    protected void onTake(int slot) {
    }
}
