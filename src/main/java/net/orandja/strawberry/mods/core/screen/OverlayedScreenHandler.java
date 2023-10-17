package net.orandja.strawberry.mods.core.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.orandja.chocoflavor.utils.MathUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class OverlayedScreenHandler extends ScreenHandler {
    private static final int NUM_COLUMNS = 9;
    private final UninteractableInventory inventory;
    private final Inventory realInventory;
    private final int rows;

    public static MutableText getName(String... chars) {
        return ((MutableText) Text.of(Arrays.stream(chars).map(it -> it.toCharArray()[0] + "").collect(Collectors.joining("")))).formatted(Formatting.WHITE);
    }

    public OverlayedScreenHandler(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, Inventory inventory, int rows, RedirectSlot... slots) {
        super(type, syncId);
        int k;
        int j;
        Map<Integer, Slot> redirectMap = new HashMap<>();
        for (RedirectSlot slot : slots) {
            redirectMap.put(slot.fakeIndex, slot);
        }
        this.realInventory = inventory;
        this.inventory = new UninteractableInventory(rows * 9, this.realInventory, slots);
//        GenericContainerScreenHandler.checkSize(inventory, rows * 9);
        this.rows = rows;
        this.inventory.onOpen(playerInventory.player);
        this.realInventory.onOpen(playerInventory.player);
        int i = (this.rows - 4) * 18;
        for (j = 0; j < this.rows; ++j) {
            for (k = 0; k < 9; ++k) {
                int index = k + j * 9;
                this.addSlot(redirectMap.getOrDefault(index, new UninteractableSlot(this.inventory, index, 8 * k * 18, 18 + j * 18)));
            }
        }

        MathUtils.grid(9, 3, (x, y) -> this.addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18)));
        MathUtils.grid(9, (x, y) -> this.addSlot(new Slot(playerInventory, x, 8 + x * 18, 142)));
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = this.slots.get(slot);
        if (slot2 != null && slot2.hasStack()) {
            ItemStack itemStack2 = slot2.getStack();
            itemStack = itemStack2.copy();
            if (slot < this.rows * 9 ? !this.insertItem(itemStack2, this.rows * 9, this.slots.size(), true) : !this.insertItem(itemStack2, 0, this.rows * 9, false)) {
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) {
                slot2.setStack(ItemStack.EMPTY);
            } else {
                slot2.markDirty();
            }
        }
        return itemStack;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.inventory.onClose(player);
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public int getRows() {
        return this.rows;
    }

}
