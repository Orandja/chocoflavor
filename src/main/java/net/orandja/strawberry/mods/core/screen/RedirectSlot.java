package net.orandja.strawberry.mods.core.screen;

import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.Slot;

public class RedirectSlot extends Slot {

    public final int fakeIndex;
    public final int realIndex;

    public RedirectSlot(Inventory inventory, int fakeIndex, int realIndex) {
        super(inventory, realIndex, 0, 0);
        this.fakeIndex = fakeIndex;
        this.realIndex = realIndex;
    }

    @Override
    public int getIndex() {
        return this.fakeIndex;
    }
}
