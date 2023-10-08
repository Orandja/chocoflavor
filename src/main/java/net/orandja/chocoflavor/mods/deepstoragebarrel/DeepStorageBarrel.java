package net.orandja.chocoflavor.mods.deepstoragebarrel;

import net.minecraft.block.BarrelBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.orandja.chocoflavor.mods.core.BlockWithEnchantment;
import net.orandja.chocoflavor.mods.core.EnchantMore;
import net.orandja.chocoflavor.mods.core.accessor.ItemFrameEntityAccessor;
import net.orandja.chocoflavor.utils.MathUtils;
import net.orandja.chocoflavor.utils.StackUtils;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public interface DeepStorageBarrel extends BlockWithEnchantment {

    class DeepStorageSlot extends Slot {
        private final int realIndex;

        public DeepStorageSlot(Inventory inventory, int realIndex, int index, int x, int y) {
            super(inventory, index, x, y);
            this.realIndex = realIndex;
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return this.inventory.getStack(0).isEmpty() || this.inventory.getStack(0).getItem() == stack.getItem();
        }

        @Override
        public ItemStack getStack() {
            return this.inventory.getStack(realIndex);
        }

        @Override
        public void setStack(ItemStack stack) {
            this.inventory.setStack(this.realIndex, stack);
            this.markDirty();
        }

        @Override
        public ItemStack takeStack(int amount) {
            return inventory.removeStack(this.realIndex, amount);
        }
    }

    class DeepStorageScreenHandler extends ScreenHandler {

        private final Inventory inventory;

        protected DeepStorageScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
            super(ScreenHandlerType.GENERIC_9X6, syncId);
            this.inventory = inventory;

            checkSize(inventory, 54);
            inventory.onOpen(playerInventory.player);

            MathUtils.grid(9, 5, (x, y) -> addSlot(new DeepStorageSlot(inventory, x + (y * 9), x + (y * 9), 8 + (x * 18), 18 + (y * 18))));
            MathUtils.grid(9, (x, y) -> addSlot(new DeepStorageSlot(inventory, (inventory.size() - 9) + x, x + 18, 8 + (x * 18), 18 + (2 * 18))));

            MathUtils.grid(9, 3, (x, y) -> addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18)));
            MathUtils.grid(9, (x, y) -> addSlot(new Slot(playerInventory, x, 8 + x * 18, 142)) );
        }

        @Override
        public boolean canUse(PlayerEntity player) {
            return inventory.canPlayerUse(player);
        }

        @Override
        public ItemStack quickMove(PlayerEntity player, int index) {
            Slot slot = slots.get(index);
            if(!slot.hasStack()) {
                return ItemStack.EMPTY;
            }

            ItemStack itemStack2 = slot.getStack();
            ItemStack itemStack = itemStack2.copy();
            if(index < 54) {
                if(!insertItem(itemStack2, 54, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if(!insertItem(itemStack2, 0, 54, false)) {
                return ItemStack.EMPTY;
            }

            slot.markDirty();

            return itemStack;
        }

        @Override
        public void onClosed(PlayerEntity player) {
            super.onClosed(player);
            inventory.onClose(player);
        }
    }

    class DeepStorageDefaultedList extends DefaultedList<ItemStack> {

        private final List<ItemStack> delegate;
        private final ItemStack defaultEntry;
        private final int displaySize;
        private final int movingSize;

        public static DeepStorageDefaultedList ofSize(int movingSize, int displaySize, ItemStack defaultEntry) {
            ItemStack[] objects = new ItemStack[movingSize];
            Arrays.fill(objects, defaultEntry);
            return new DeepStorageDefaultedList(movingSize, displaySize, defaultEntry, new ArrayList<>(Arrays.asList(objects)));
        }

        public DeepStorageDefaultedList(int movingSize, int displaySize, ItemStack defaultEntry, List<ItemStack> delegate) {
            super(delegate, defaultEntry);
            this.movingSize = movingSize;
            this.displaySize = displaySize;
            this.defaultEntry = defaultEntry;
            this.delegate = delegate;
        }

        @Override
        public ItemStack set(int index, ItemStack element) {
            if(element.isEmpty()) {
                this.delegate.remove(index);
                this.delegate.add(defaultEntry);
                return delegate.set(this.movingSize - 1, defaultEntry);
            }

            int iIndex = index;
            while(iIndex > 0 && delegate.get(iIndex - 1).isEmpty()) {
                iIndex--;
            }
            return delegate.set(iIndex, element);
        }

        void markDirty() {
            this.set(0, this.get(0));
        }
    }

//    Enchantment[] CAPACITY = new Enchantment[] { Enchantments.EFFICIENCY };
    EnchantmentArraySetting CAPACITY = new EnchantmentArraySetting("barrel.capacity.enchantments", new Enchantment[] { Enchantments.EFFICIENCY });
//    Enchantment[] ENABLING = new Enchantment[] { Enchantments.INFINITY };
    EnchantmentArraySetting ENABLING = new EnchantmentArraySetting("barrel.enabling.enchantments", new Enchantment[] { Enchantments.INFINITY });

    Enchantment[] VALID_ENCHANTMENTS = BlockWithEnchantment.concat(CAPACITY, ENABLING);

    Box BOX = new Box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);

    static void tick(World world, BlockPos pos, BlockState state, BarrelBlockEntity barrel) {
        if(world.getTime() % 30L != 0L) {
            return;
        }

        Box box = BOX.offset(pos.offset(state.get(BarrelBlock.FACING)));
        List<ItemFrameEntity> itemFrames = world.getNonSpectatingEntities(ItemFrameEntity.class, box);
        if(itemFrames.isEmpty()) {
            return;
        }

        ItemFrameEntity itemFrame = itemFrames.get(0);
        if(barrel instanceof DeepStorageBarrel deepStorageBarrel) {
            if(StackUtils.isOfItem(itemFrame.getHeldItemStack(), barrel.getStack(0))) {
                itemFrame.getHeldItemStack().setCustomName(Text.of(StackUtils.wholeCount(deepStorageBarrel.getInventory()) + " ").copy().append(barrel.getStack(0).getName()));
                ItemStack copyStack = itemFrame.getHeldItemStack().copy();
                copyStack.setHolder(itemFrame);
                itemFrame.getDataTracker().set(((ItemFrameEntityAccessor)itemFrame).getITEM_STACK(), copyStack);
            }
        }
    }

    static void beforeLaunch() {
        EnchantMore.addComplex(Items.BARREL, (enchantment, stack) -> {
            if(stack.getCount() == 1) {
                if(ENABLING.contains(enchantment)) {
                    return true;
                }

                if(CAPACITY.contains(enchantment)) {
                    return ENABLING.anyMatch(it -> EnchantmentHelper.getLevel(it, stack) > 0) && stack.hasEnchantments();
                }
            }

            return false;
        });
    }

    DefaultedList<ItemStack> getInventory();
    void setInventory(DefaultedList<ItemStack> value);

    default void createBarrelScreenHandler(Object object, int syncId, PlayerInventory playerInventory, CallbackInfoReturnable<ScreenHandler> info) {
        if(this.getEnchantmentDictionary().hasAnyEnchantment(ENABLING) && object instanceof BarrelBlockEntity barrel) {
            info.setReturnValue(new DeepStorageScreenHandler(syncId, playerInventory, barrel));
        }
    }

    default void saveEnchantments(NbtCompound tag, CallbackInfo info, Consumer<NbtCompound> superWrite) {
        if(this.getEnchantmentDictionary().hasAnyEnchantment(ENABLING)) {
            this.getEnchantmentDictionary().saveToNbt(tag);
            StackUtils.toNBT(this.getInventory(), tag);
            superWrite.accept(tag);
            info.cancel();
        }
    }

    default void loadEnchantments(NbtCompound tag, CallbackInfo info, Consumer<NbtCompound> superRead) {
        this.getEnchantmentDictionary().loadFromNbt(tag);
        if(this.getEnchantmentDictionary().hasAnyEnchantment(ENABLING)) {
            this.setInventory(DeepStorageDefaultedList.ofSize(27 + this.getEnchantmentDictionary().computeValue(it -> it * 27, VALID_ENCHANTMENTS), 27, ItemStack.EMPTY));
            StackUtils.fromNBT(this.getInventory(), tag);
            superRead.accept(tag);
            info.cancel();
        }
    }

    default void getBarrelSize(CallbackInfoReturnable<Integer> info) {
        info.setReturnValue(this.getInventory().size());
    }

    default boolean isValidForBarrel(int slot, ItemStack stack) {
        return !this.getEnchantmentDictionary().hasEnchantments() || (!(Block.getBlockFromItem(stack.getItem()) instanceof ShulkerBoxBlock) && this.getInventory().get(0).isEmpty() || this.getInventory().get(0).getItem() == stack.getItem());
    }

    default void onMarkDirty() {
        if(this.getInventory() instanceof DeepStorageDefaultedList inventory) {
            inventory.markDirty();
        }
    }

    default void applyEnchantments() {
        this.setInventory(DeepStorageDefaultedList.ofSize(27 + this.getEnchantmentDictionary().computeValue(it -> it * 27, VALID_ENCHANTMENTS), 27, ItemStack.EMPTY));
    }
}
