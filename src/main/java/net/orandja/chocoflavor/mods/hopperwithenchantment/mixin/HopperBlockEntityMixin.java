package net.orandja.chocoflavor.mods.hopperwithenchantment.mixin;

import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.Hopper;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.state.property.Property;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import net.orandja.chocoflavor.mods.hopperwithenchantment.HopperWithEnchantment;
import net.orandja.chocoflavor.mods.core.BlockWithEnchantment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Mixin(HopperBlockEntity.class)
public abstract class HopperBlockEntityMixin extends LockableContainerBlockEntity implements HopperWithEnchantment {

    protected HopperBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Inject(method = "insertAndExtract", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/block/entity/HopperBlockEntity;setTransferCooldown(I)V"))
    private static void insertAndExtract(World world, BlockPos pos, BlockState state, HopperBlockEntity blockEntity, BooleanSupplier booleanSupplier, CallbackInfoReturnable<Boolean> info) {
        BlockWithEnchantment.computeWithValue(blockEntity, HopperWithEnchantment.class, SPEED.getValue(), (hopper, lvl) -> hopper.vw$setHopperCooldown(Math.max(1, (int)(8 - (1.2D * lvl)))));
    }

    @Inject(method = "setTransferCooldown", at = @At("HEAD"), cancellable = true)
    public void setTransferCooldown(int transferCooldown, CallbackInfo info) {
        if(transferCooldown > 0) {
            BlockWithEnchantment.computeWithValue(this, HopperWithEnchantment.class, SPEED.getValue(), (hopper, lvl) -> {
                this.transferCooldown = Math.max(1, (int) (transferCooldown - (1.2D * lvl)));
                info.cancel();
            });
        }
    }

    // Extracting from a Hopper with FILTER property
    @Inject(method = "extract(Lnet/minecraft/world/World;Lnet/minecraft/block/entity/Hopper;)Z",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/HopperBlockEntity;isInventoryEmpty(Lnet/minecraft/inventory/Inventory;Lnet/minecraft/util/math/Direction;)Z", shift = At.Shift.BEFORE),
        cancellable = true
    )
    private static void filterIsInventoryEmpty(World world, Hopper hopper, CallbackInfoReturnable<Boolean> info) {
        Inventory inventory = getInputInventory(world, hopper);
        if (inventory != null) {
            Direction direction = Direction.DOWN;
            BlockWithEnchantment.computeWithValue(inventory, HopperWithEnchantment.class, FILTER.getValue(), (it, lvl) -> {
                info.setReturnValue(getAvailableSlots(inventory, direction).anyMatch((slot) -> {
                    ItemStack stack = inventory.getStack(slot);
                    if(stack.isEmpty() || stack.getCount() == 1) {
                        return false;
                    }
                    return extract(hopper, inventory, slot, direction);
                }));
            });
        }
    }

    @Redirect(method = "insert",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z", ordinal = 0)
    )
    private static boolean checkForFilter(ItemStack stack, World world, BlockPos pos, BlockState state, Inventory inventory) {
        return BlockWithEnchantment.getValue(inventory, HopperWithEnchantment.class, FILTER.getValue(), (it, lvl) -> stack.isEmpty() || stack.getCount() == 1, stack.isEmpty());
    }

//    @Redirect(method = "extract(Lnet/minecraft/world/World;Lnet/minecraft/block/entity/Hopper;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/HopperBlockEntity;getAvailableSlots(Lnet/minecraft/inventory/Inventory;Lnet/minecraft/util/math/Direction;)Ljava/util/stream/IntStream;"))
//    private static IntStream filterGetAvailableSlots(Inventory inventory, Direction facing) {
//        IntStream intStream;
//        if(inventory instanceof SidedInventory sidedInventory) {
//            intStream = IntStream.of(sidedInventory.getAvailableSlots(facing));
//        } else {
//            intStream = IntStream.range(0, inventory.size());
//        }
//        return BlockWithEnchantment.getValue(inventory, HopperWithEnchantment.class, FILTER.getValue(), (it, lvl) -> {
//            return intStream.filter(id -> !inventory.getStack(id).isEmpty() || inventory.getStack(id).getCount() == 1);
//        }, intStream);
//    }

    @Inject(method = "transfer(Lnet/minecraft/inventory/Inventory;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/math/Direction;)Lnet/minecraft/item/ItemStack;",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/Inventory;size()I"),
        cancellable = true
    )
    private static void checkForMending(Inventory from, Inventory to, ItemStack stack, Direction side, CallbackInfoReturnable<ItemStack> info) {
        BlockWithEnchantment.compute(from, HopperWithEnchantment.class, OUTPUT.getValue(), it -> {
            if(from instanceof HopperBlockEntity hopper) {
                int redstonePower = hopper.getWorld().getReceivedRedstonePower(hopper.getPos());
                if(redstonePower > 0) {
                    info.setReturnValue(transfer(from, to, stack, redstonePower - 1, side));
                } else {
                    info.setReturnValue(stack);
                }
            }
        });
    }

    @Shadow
    private static ItemStack transfer(@Nullable Inventory from, Inventory to, ItemStack stack, int slot, @Nullable Direction side) {
        return null;
    }

    @Redirect(method = "insertAndExtract", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;get(Lnet/minecraft/state/property/Property;)Ljava/lang/Comparable;"))
    private static Comparable<?> redstoneEnabled(BlockState state2, Property<Boolean> value, World world, BlockPos pos, BlockState state, HopperBlockEntity blockEntity, BooleanSupplier booleanSupplier) {
        boolean isEnabled = state.get(HopperBlock.ENABLED);
        return BlockWithEnchantment.getValue(world.getBlockEntity(pos), HopperWithEnchantment.class, OUTPUT.getValue(), it -> !isEnabled, isEnabled);
    }

    @Inject(method = "extract(Lnet/minecraft/world/World;Lnet/minecraft/block/entity/Hopper;)Z", at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/block/entity/HopperBlockEntity;getInputInventory(Lnet/minecraft/world/World;Lnet/minecraft/block/entity/Hopper;)Lnet/minecraft/inventory/Inventory;"))
    private static void enableOffset(World world, Hopper hopper, CallbackInfoReturnable<Boolean> info) {
        if(hopper instanceof HopperWithEnchantment hopperWithEnchantment)
            hopperWithEnchantment.vw$setShouldOffset(true);
    }
    @Inject(method = "extract(Lnet/minecraft/world/World;Lnet/minecraft/block/entity/Hopper;)Z", at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/block/entity/HopperBlockEntity;getInputItemEntities(Lnet/minecraft/world/World;Lnet/minecraft/block/entity/Hopper;)Ljava/util/List;"))
    private static void disableOffset(World world, Hopper hopper, CallbackInfoReturnable<Boolean> info) {
        if(hopper instanceof HopperWithEnchantment hopperWithEnchantment)
            hopperWithEnchantment.vw$setShouldOffset(false);
    }

    @Redirect(method = "getInputItemEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/shape/VoxelShape;getBoundingBoxes()Ljava/util/List;"))
    private static List<Box> expandSweeping(VoxelShape instance, World world, Hopper hopper) {
        return BlockWithEnchantment.getValue(hopper, HopperWithEnchantment.class, RANGE.getValue(), (it, lvl) -> {
            return instance.getBoundingBoxes().stream().map(box -> box.expand(1.0 * lvl, 0.0, 1.0 * lvl)).collect(Collectors.toList());
        }, instance.getBoundingBoxes());
    }

    @Inject(method = "getInputItemEntities", at = @At("HEAD"), cancellable = true)
    private static void knockbackInputEntites(World world, Hopper hopper, CallbackInfoReturnable<List<ItemEntity>> info) {
        BlockWithEnchantment.computeWithValue(hopper, HopperWithEnchantment.class, OFFSET.getValue(), (it, lvl) -> {
            info.setReturnValue(Hopper.ABOVE_SHAPE.getBoundingBoxes().stream().flatMap((box) -> {
                return world.getEntitiesByClass(ItemEntity.class, box.offset(hopper.getHopperX() - 0.5, hopper.getHopperY() - 0.5 + (lvl * 1.0D), hopper.getHopperZ() - 0.5), EntityPredicates.VALID_ENTITY).stream();
            }).collect(Collectors.toList()));
        });
    }

    @Inject(method = "onEntityCollided", at = @At("HEAD"), cancellable = true)
    private static void onEntityCollided(World world, BlockPos pos, BlockState state, Entity entity, HopperBlockEntity blockEntity, CallbackInfo info) {
        BlockWithEnchantment.compute(world.getBlockEntity(pos), HopperWithEnchantment.class, OFFSET.getValue(), it -> info.cancel());
    }

    @Override public Inventory vw$getOutInventory(@NotNull World world, @NotNull BlockPos pos, @NotNull BlockState state) {return getOutputInventory(world, pos, state);}

    @Override public boolean vw$isInvFull(@NotNull Inventory inventory, @NotNull Direction direction) {return isInventoryFull(inventory, direction);}

    @Override public @NotNull IntStream vw$getOutputAvailableSlots(@NotNull Inventory inventory, @NotNull Direction side) {return getAvailableSlots(inventory, side);}

    @Shadow private static native Inventory getOutputInventory(World world, BlockPos pos, BlockState state);
    @Shadow private static native boolean isInventoryFull(Inventory inventory, Direction direction);
    @Shadow private static native IntStream getAvailableSlots(Inventory inventory, Direction side);

    @Shadow private native boolean isFull();

    @Override public boolean vw$isHopperFull() {return isFull();}

    @Override public boolean vw$isHopperEmpty() {return isEmpty();}

    @Override public void vw$markHopperDirty(@NotNull World world, @NotNull BlockPos pos, @NotNull BlockState state) {markDirty(world, pos, state);}

    @Shadow private native void setTransferCooldown(int cooldown);

    @Override public void vw$setHopperCooldown(int cooldown) {setTransferCooldown(cooldown);}

    @Shadow protected native DefaultedList<ItemStack> getInvStackList();

    @Override public DefaultedList<ItemStack> vw$getInvList() {return getInvStackList();}

    @Shadow public abstract double getHopperX();

    @Shadow public abstract double getHopperY();
    @Inject(method = "getHopperY", at = @At("HEAD"), cancellable = true)
    public void offsetHopperY(CallbackInfoReturnable<Double> info) {
        info.setReturnValue((double)this.pos.getY() + 0.5 + (vw$getShouldOffset() ?
                BlockWithEnchantment.getValue(this, HopperWithEnchantment.class, OFFSET.getValue(), it -> it.getEnchantmentDictionary().computeValue(lvl -> lvl * 1.0, OFFSET.getValue()), 0)
            : 0));
    }

    @Shadow public abstract double getHopperZ();

    @Shadow @Nullable private static Inventory getInputInventory(World world, Hopper hopper) {
        return null;
    }

    @Shadow private static boolean extract(Hopper hopper, Inventory inventory, int slot, Direction side) {
        return false;
    }

    @Shadow private int transferCooldown;

    @Inject(method = "readNbt", at = @At("HEAD"))
    public void readNbt(NbtCompound tag, CallbackInfo info) {
        getEnchantmentDictionary().loadFromNbt(tag);
    }

    @Inject(method = "writeNbt", at = @At("HEAD"))
    public void writeNbt(NbtCompound tag, CallbackInfo info) {
        getEnchantmentDictionary().saveToNbt(tag);
    }

    @Unique @Getter EnchantmentDictionary enchantmentDictionary = new EnchantmentDictionary(VALID_ENCHANTMENTS);
    @Unique private boolean shouldOffset = false;

    @Override
    public boolean vw$getShouldOffset() {
        return shouldOffset;
    }

    @Override
    public void vw$setShouldOffset(boolean value) {
        shouldOffset = value;
    }
}
