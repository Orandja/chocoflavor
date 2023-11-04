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
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.orandja.chocoflavor.mods.core.BlockWithEnchantment;
import net.orandja.chocoflavor.mods.core.EnchantMore;
import net.orandja.chocoflavor.mods.core.accessor.ItemFrameEntityAccessor;
import net.orandja.chocoflavor.utils.StackUtils;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface DeepStorageBarrel extends BlockWithEnchantment {

    EnchantmentArraySetting CAPACITY = new EnchantmentArraySetting("barrel.capacity.enchantments", new Enchantment[] { Enchantments.EFFICIENCY });

    EnchantmentArraySetting ENABLING = new EnchantmentArraySetting("barrel.enabling.enchantments", new Enchantment[] { Enchantments.INFINITY });

    EnchantmentArraySetting DISPOSE_EXCESS = new EnchantmentArraySetting("barrel.excess.enchantments", new Enchantment[] { Enchantments.FLAME });

    Enchantment[] VALID_ENCHANTMENTS = BlockWithEnchantment.concat(CAPACITY, ENABLING, DISPOSE_EXCESS);

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

                if(DISPOSE_EXCESS.contains(enchantment)) {
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

    default void setStack(int slot, ItemStack stack, BiConsumer<Integer, ItemStack> superSetStack) {
        if(this.getEnchantmentDictionary().hasAnyEnchantment(DISPOSE_EXCESS) && this.getInventory() instanceof DeepStorageDefaultedList inventory) {
            if(slot == inventory.size() - 1) {
                superSetStack.accept(slot, ItemStack.EMPTY);
                return;
            }
        }
        superSetStack.accept(slot, stack);
    }

    default boolean hasEnchantments() {
        return this.getEnchantmentDictionary().hasEnchantments();
    }
}
