package net.orandja.chocoflavor;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.Hopper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import net.orandja.chocoflavor.enchantment.EnchantmentArraySetting;
import net.orandja.chocoflavor.enchantment.EnchantmentDictionary;
import net.orandja.chocoflavor.enchantment.EnchantmentDictionaryUtils;
import net.orandja.chocoflavor.utils.GlobalUtils;
import net.orandja.chocoflavor.utils.StackUtils;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ChocoHoppers {
    private ChocoHoppers() {}

    public static final EnchantmentArraySetting SPEED = new EnchantmentArraySetting("hopper.speed.enchantments", new Enchantment[] { Enchantments.EFFICIENCY });

    public static final EnchantmentArraySetting FILTER = new EnchantmentArraySetting("hopper.filter.enchantments", new Enchantment[] { Enchantments.SILK_TOUCH});

    public static final EnchantmentArraySetting OUTPUT = new EnchantmentArraySetting("hopper.output.enchantments", new Enchantment[] { Enchantments.MENDING });

    public static final EnchantmentArraySetting OFFSET = new EnchantmentArraySetting("hopper.offset.enchantments", new Enchantment[] { Enchantments.KNOCKBACK });

    public static final EnchantmentArraySetting RANGE = new EnchantmentArraySetting("hopper.range.enchantments", new Enchantment[] { Enchantments.SWEEPING });
    public static final Enchantment[] VALID_ENCHANTMENTS = EnchantmentDictionaryUtils.concat(SPEED.getValue(), FILTER.getValue(), OUTPUT.getValue(), OFFSET.getValue(), RANGE.getValue());

    public static void init() {
        ChocoEnchantments.createRegistry(Items.HOPPER)
                .allowInAnvil(VALID_ENCHANTMENTS);
    }

    public interface Handler extends ChocoEnchantments.BlockHandler, Inventory {
        default EnchantmentDictionary createDictionary() {
            return new EnchantmentDictionary(VALID_ENCHANTMENTS);
        }

        double getHopperX();
        double getHopperY();
        double getHopperZ();

        DefaultedList<ItemStack> getInventory();

        default void repeatInsert(World world, BlockPos pos, BlockState state, Inventory inventory) {
            for(int i = 0; i < getDictionary().getValue(SPEED.getValue()); i++) {
                if(!doInsert(world, pos, state, inventory)) {
                    break;
                }
            }
        }

        default void repeatExtract(World world, Hopper hopper) {
            for(int i = 0; i < getDictionary().getValue(SPEED.getValue()); i++) {
                if(!doExtract(world, hopper)) {
                    break;
                }
            }
        }

        boolean doInsert(World world, BlockPos pos, BlockState state, Inventory inventory);
        boolean doExtract(World world, Hopper hopper);
        boolean doExtract(Hopper hopper, Inventory inventory, int slot, Direction side);

        default boolean isStackValidForTransfer(ItemStack stack) {
            if(getDictionary().hasAnyEnchantment(FILTER)) {
                if(getInventory().get(0).getMaxCount() == 1) {
                    return getInventory().get(0) != stack;
                }
                return StackUtils.hasMinimum(stack, 2);
            }
            return true;
        }

        IntStream availableSlots(Inventory inventory, Direction side);

        default void checkCanExtract(ItemStack stack, CallbackInfoReturnable<Boolean> info) {
            if(getDictionary().hasAnyEnchantment(FILTER)) {
                if(getInventory().get(0).getMaxCount() == 1) {
                    info.setReturnValue(StackUtils.isSimilar(getInventory().get(0), stack));
                }
            }
        }

        default void checkHopperOver(Hopper hopper, CallbackInfoReturnable<Boolean> info) {
            Direction direction = Direction.DOWN;
            if(getDictionary().hasAnyEnchantment(FILTER)) {
                info.setReturnValue(availableSlots(this, direction).anyMatch((slot) -> {
                    if(getInventory().get(0).getMaxCount() == 1) {
                        return getStack(slot).isEmpty() && doExtract(hopper, this, slot, direction);
                    }
                    return StackUtils.hasMinimum(getStack(slot), 2) && doExtract(hopper, this, slot, direction);
                }));
            }
        }

        default void cancelCollide(CallbackInfo info) {
            if(getDictionary().hasAnyEnchantment(OFFSET)) {
                info.cancel();
            }
        }

        default Function<Box, Stream<ItemEntity>> offsetMapper(World world, Function<Box, Stream<ItemEntity>> mapper) {
            if(getDictionary().hasAnyEnchantment(OFFSET)) {
                return (box -> world.getEntitiesByClass(ItemEntity.class, box.offset(getHopperX() - 0.5, getHopperY() + getDictionary().getValue(OFFSET.getValue()) - 0.5, getHopperZ() - 0.5), EntityPredicates.VALID_ENTITY).stream());
            }
            return mapper;
        }

        Inventory getHopperInventoryAt(World world, double x, double y, double z);
        default Inventory getOffsetInventoryAt(Inventory inventory, World world) {
            if(getDictionary().hasAnyEnchantment(OFFSET)) {
                return getHopperInventoryAt(world, getHopperX(), getHopperY() + getDictionary().getValue(OFFSET.getValue()) + 1.0, getHopperZ());
            }
            return inventory;
        }

        default List<Box> extendInputBox(VoxelShape instance) {
            if(getDictionary().hasAnyEnchantment(RANGE)) {
                int lvl = getDictionary().getValue(RANGE.getValue());
                return GlobalUtils.apply(Hopper.ABOVE_SHAPE.getBoundingBoxes().stream().map(box -> box.expand(lvl, 0.0, lvl)).collect(Collectors.toList()), expandedList -> {
                    expandedList.addAll(Hopper.INSIDE_SHAPE.getBoundingBoxes());
                });
            }
            return instance.getBoundingBoxes();
        }
    }
}
