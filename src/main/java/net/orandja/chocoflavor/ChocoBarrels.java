package net.orandja.chocoflavor;

import net.minecraft.block.BarrelBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
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
import net.orandja.chocoflavor.enchantment.EnchantmentArraySetting;
import net.orandja.chocoflavor.enchantment.EnchantmentDictionary;
import net.orandja.chocoflavor.enchantment.EnchantmentDictionaryUtils;
import net.orandja.chocoflavor.inventory.DeepStorageDefaultedList;
import net.orandja.chocoflavor.inventory.DeepStorageScreenHandler;
import net.orandja.chocoflavor.accessor.ItemFrameEntityAccessor;
import net.orandja.chocoflavor.utils.StackUtils;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class ChocoBarrels {
    private ChocoBarrels() {}

    public static void init() {

        ChocoEnchantments.createRegistry(Items.BARREL)
            .allowInAnvil((stack, enchantment) -> {
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

    public static final EnchantmentArraySetting CAPACITY = new EnchantmentArraySetting("barrel.capacity.enchantments", new Enchantment[] { Enchantments.EFFICIENCY });

    public static final EnchantmentArraySetting ENABLING = new EnchantmentArraySetting("barrel.enabling.enchantments", new Enchantment[] { Enchantments.INFINITY });

    public static final EnchantmentArraySetting DISPOSE_EXCESS = new EnchantmentArraySetting("barrel.excess.enchantments", new Enchantment[] { Enchantments.FLAME });

    public static final Enchantment[] VALID_ENCHANTMENTS = EnchantmentDictionaryUtils.concat(CAPACITY, ENABLING, DISPOSE_EXCESS);

    public static final Box BOX = new Box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);

    public interface Handler extends ChocoEnchantments.BlockHandler {

        default EnchantmentDictionary createDictionary() {
            return new EnchantmentDictionary(VALID_ENCHANTMENTS);
        }

        DefaultedList<ItemStack> getInventory();
        void setInventory(DefaultedList<ItemStack> value);

        default void createBarrelScreenHandler(Object object, int syncId, PlayerInventory playerInventory, CallbackInfoReturnable<ScreenHandler> info) {
            if(this.getDictionary().hasAnyEnchantment(ENABLING) && object instanceof BarrelBlockEntity barrel) {
                info.setReturnValue(new DeepStorageScreenHandler(syncId, playerInventory, barrel));
            }
        }

        default void saveEnchantments(NbtCompound tag, CallbackInfo info, Consumer<NbtCompound> superWrite) {
            if(this.getDictionary().hasAnyEnchantment(ENABLING)) {
                this.getDictionary().saveToNbt(tag);
                StackUtils.toNBT(this.getInventory(), tag);
                superWrite.accept(tag);
                info.cancel();
            }
        }

        default void loadEnchantments(NbtCompound tag, CallbackInfo info, Consumer<NbtCompound> superRead) {
            this.getDictionary().loadFromNbt(tag);
            if(this.getDictionary().hasAnyEnchantment(ENABLING)) {
                this.setInventory(DeepStorageDefaultedList.ofSize(27 + this.getDictionary().computeValue(it -> it * 27, VALID_ENCHANTMENTS), 27, ItemStack.EMPTY));
                StackUtils.fromNBT(this.getInventory(), tag);
                superRead.accept(tag);
                info.cancel();
            }
        }


        default void getBarrelSize(CallbackInfoReturnable<Integer> info) {
            info.setReturnValue(this.getInventory().size());
        }

        default boolean isValidForBarrel(int slot, ItemStack stack) {
            return !this.getDictionary().hasEnchantments() || (!(Block.getBlockFromItem(stack.getItem()) instanceof ShulkerBoxBlock) && this.getInventory().get(0).isEmpty() || this.getInventory().get(0).getItem() == stack.getItem());
        }

        default void applyEnchantments() {
            this.setInventory(DeepStorageDefaultedList.ofSize(27 + this.getDictionary().computeValue(it -> it * 27, VALID_ENCHANTMENTS), 27, ItemStack.EMPTY));
        }

        default void setStack(int slot, ItemStack stack, BiConsumer<Integer, ItemStack> superSetStack) {
            if(this.getDictionary().hasAnyEnchantment(DISPOSE_EXCESS) && this.getInventory() instanceof DeepStorageDefaultedList inventory) {
                if(slot == inventory.size() - 1) {
                    superSetStack.accept(slot, ItemStack.EMPTY);
                    return;
                }
            }
            superSetStack.accept(slot, stack);
        }

        default void getReverseRange(CallbackInfoReturnable<IntStream> info) {
            if(getDictionary().hasEnchantments()) {
                info.setReturnValue(IntStream.range(0, getInventory().size()).map(it -> getInventory().size() - 1 - it));
            }
        }

        @Override
        default <T extends BlockEntity> BlockEntityTicker<T> getCustomTicker() {
            return ChocoBarrels::tick;
        }
    }

    static void tick(World world, BlockPos pos, BlockState state, BlockEntity entity) {
        if(world.getTime() % 30L != 0L) {
            return;
        }

        if(entity instanceof BarrelBlockEntity barrel && barrel instanceof Handler handler) {
            Box box = BOX.offset(pos.offset(state.get(BarrelBlock.FACING)));
            List<ItemFrameEntity> itemFrames = world.getNonSpectatingEntities(ItemFrameEntity.class, box);
            if(itemFrames.isEmpty()) {
                return;
            }

            ItemFrameEntity itemFrame = itemFrames.get(0);
            if(StackUtils.isOfItem(itemFrame.getHeldItemStack(), barrel.getStack(0))) {
                itemFrame.getHeldItemStack().setCustomName(Text.of(StackUtils.wholeCount(handler.getInventory()) + " ").copy().append(barrel.getStack(0).getName()));
                ItemStack copyStack = itemFrame.getHeldItemStack().copy();
                copyStack.setHolder(itemFrame);
                itemFrame.getDataTracker().set(((ItemFrameEntityAccessor)itemFrame).getITEM_STACK(), copyStack);
            }
        }
    }

}
