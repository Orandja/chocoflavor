//package net.orandja.chocoflavor.mods.blockswithenchantments.inventory;
//
//import net.minecraft.item.ItemStack;
//import net.minecraft.util.collection.DefaultedList;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//public class DeepStorageDefaultedList extends DefaultedList<ItemStack> {
//
//    private final List<ItemStack> delegate;
//    private final ItemStack defaultEntry;
//    private final int displaySize;
//    private final int movingSize;
//
//    public static DeepStorageDefaultedList ofSize(int movingSize, int displaySize, ItemStack defaultEntry) {
//        ItemStack[] objects = new ItemStack[movingSize];
//        Arrays.fill(objects, defaultEntry);
//        return new DeepStorageDefaultedList(movingSize, displaySize, defaultEntry, new ArrayList<>(Arrays.asList(objects)));
//    }
//
//    public DeepStorageDefaultedList(int movingSize, int displaySize, ItemStack defaultEntry, List<ItemStack> delegate) {
//        super(delegate, defaultEntry);
//        this.movingSize = movingSize;
//        this.displaySize = displaySize;
//        this.defaultEntry = defaultEntry;
//        this.delegate = delegate;
//    }
//
//    @Override
//    public ItemStack set(int index, ItemStack element) {
//        if (element.isEmpty()) {
//            this.delegate.remove(index);
//            this.delegate.add(defaultEntry);
//            return delegate.set(this.movingSize - 1, defaultEntry);
//        }
//
//        int iIndex = index;
//        while (iIndex > 0 && delegate.get(iIndex - 1).isEmpty()) {
//            iIndex--;
//        }
//        return delegate.set(iIndex, element);
//    }
//}
