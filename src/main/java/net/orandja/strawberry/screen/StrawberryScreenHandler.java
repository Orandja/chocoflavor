package net.orandja.strawberry.screen;

import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.orandja.chocoflavor.utils.MathUtils;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public abstract class StrawberryScreenHandler extends ScreenHandler {

    public interface SlotSupplier {
        Slot create(Inventory inventory, int index, int x, int y);
    }

    private final static ScreenHandlerType<?>[] GENERICS = new ScreenHandlerType[] { ScreenHandlerType.GENERIC_9X1, ScreenHandlerType.GENERIC_9X2, ScreenHandlerType.GENERIC_9X3, ScreenHandlerType.GENERIC_9X4, ScreenHandlerType.GENERIC_9X5, ScreenHandlerType.GENERIC_9X6 };
    private static ScreenHandlerType<?> getGeneric(int rows) {
        if(rows > 0 && rows < 7) {
            return GENERICS[rows - 1];
        }
        return GENERICS[5];
    }

    protected StrawberryScreenHandler(int rows, int syncId) {
        super(getGeneric(rows), syncId);
        int k;
        int j;
//        Map<Integer, Slot> redirectMap = new HashMap<>();
//        for (RedirectSlot slot : slots) {
//            redirectMap.put(slot.fakeIndex, slot);
//        }
//        this.realInventory = inventory;
//        this.inventory = new UninteractableInventory(rows * 9, this.realInventory, slots);
////        GenericContainerScreenHandler.checkSize(inventory, rows * 9);
//        this.rows = rows;
//        this.inventory.onOpen(playerInventory.player);
//        this.realInventory.onOpen(playerInventory.player);
//        int i = (this.rows - 4) * 18;
//        for (j = 0; j < this.rows; ++j) {
//            for (k = 0; k < 9; ++k) {
//                int index = k + j * 9;
//                this.addSlot(redirectMap.getOrDefault(index, new UninteractableSlot(this.inventory, index, 8 * k * 18, 18 + j * 18)));
//            }
//        }
//
//        MathUtils.grid(9, 3, (x, y) -> this.addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18)));
//        MathUtils.grid(9, (x, y) -> this.addSlot(new Slot(playerInventory, x, 8 + x * 18, 142)));
    }
}
