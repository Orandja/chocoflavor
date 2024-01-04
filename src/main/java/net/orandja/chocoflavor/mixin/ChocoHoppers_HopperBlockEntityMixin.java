package net.orandja.chocoflavor.mixin;

import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.Hopper;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import net.orandja.chocoflavor.ChocoHoppers;
import net.orandja.chocoflavor.enchantment.EnchantmentDictionary;
import net.orandja.chocoflavor.utils.GlobalUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Mixin(HopperBlockEntity.class)
public abstract class ChocoHoppers_HopperBlockEntityMixin extends LockableContainerBlockEntity implements ChocoHoppers.Handler {

    @Unique @Getter EnchantmentDictionary dictionary = createDictionary();

    protected ChocoHoppers_HopperBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Shadow private static boolean insert(World world, BlockPos pos, BlockState state, Inventory inventory) {
        return false;
    }

    @Shadow private static boolean extract(World world, Hopper hopper) {
        return false;
    }
    @Shadow private static native boolean isInventoryFull(Inventory inventory, Direction direction);
    @Shadow private static native IntStream getAvailableSlots(Inventory inventory, Direction side);

    @Override
    public IntStream availableSlots(Inventory inventory, Direction side) {
        return getAvailableSlots(inventory, side);
    }

    @Shadow private static boolean extract(Hopper hopper, Inventory inventory, int slot, Direction side) {
        return false;
    }

    @Shadow @Nullable
    private static Inventory getInputInventory(World world, Hopper hopper) {
        return null;
    }

    @Getter @Shadow private DefaultedList<ItemStack> inventory;

    @Shadow @Nullable private static Inventory getInventoryAt(World world, double x, double y, double z) {
        return null;
    }

    @Inject(method = "insertAndExtract",
            locals = LocalCapture.CAPTURE_FAILSOFT,
            at = @At(value = "INVOKE_ASSIGN",shift = At.Shift.AFTER, target = "Lnet/minecraft/block/entity/HopperBlockEntity;insert(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/inventory/Inventory;)Z"))
    private static void afterInsert(World world, BlockPos pos, BlockState state, HopperBlockEntity blockEntity, BooleanSupplier booleanSupplier, CallbackInfoReturnable<Boolean> cir, boolean bl) {
        if(bl) {
            GlobalUtils.applyAs(blockEntity, ChocoHoppers.Handler.class, it -> it.repeatInsert(world, pos, state, blockEntity));
        }
    }

    @Inject(method = "insertAndExtract",
            locals = LocalCapture.CAPTURE_FAILSOFT,
            at = @At(value = "INVOKE",shift = At.Shift.AFTER, target = "Ljava/util/function/BooleanSupplier;getAsBoolean()Z"))
    private static void afterExtract(World world, BlockPos pos, BlockState state, HopperBlockEntity blockEntity, BooleanSupplier booleanSupplier, CallbackInfoReturnable<Boolean> cir, boolean bl) {
        GlobalUtils.applyAs(blockEntity, ChocoHoppers.Handler.class, it -> it.repeatExtract(world, blockEntity));
    }

    @Inject(method = "canExtract",
        at = @At("HEAD"),
        cancellable = true)
    private static void filterCanExtract(Inventory hopperInventory, Inventory fromInventory, ItemStack stack, int slot, Direction facing, CallbackInfoReturnable<Boolean> info) {
        GlobalUtils.applyAs(hopperInventory, ChocoHoppers.Handler.class, it -> it.checkCanExtract(stack, info));
    }

    @Override
    public boolean doInsert(World world, BlockPos pos, BlockState state, Inventory inventory) {
        return insert(world, pos, state, inventory);
    }

    @Override
    public boolean doExtract(World world, Hopper hopper) {
        return extract(world, hopper);
    }

    @Override
    public boolean doExtract(Hopper hopper, Inventory inventory, int slot, Direction side) {
        return extract(hopper, inventory, slot, side);
    }

    // Extracting from another Hopper with FILTER property
    @Inject(method = "extract(Lnet/minecraft/world/World;Lnet/minecraft/block/entity/Hopper;)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/HopperBlockEntity;isInventoryEmpty(Lnet/minecraft/inventory/Inventory;Lnet/minecraft/util/math/Direction;)Z", shift = At.Shift.BEFORE),
            cancellable = true
    )
    private static void filterIsInventoryEmpty(World world, Hopper hopper, CallbackInfoReturnable<Boolean> info) {
        GlobalUtils.applyAs(getInputInventory(world, hopper), ChocoHoppers.Handler.class, it -> it.checkHopperOver(hopper, info));
    }

    @Redirect(method = "insert",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z", ordinal = 0)
    )
    private static boolean checkForFilter(ItemStack stack, World world, BlockPos pos, BlockState state, Inventory inventory) {
        return GlobalUtils.runAsWithDefault(inventory, ChocoHoppers.Handler.class, stack.isEmpty(), it -> !it.isStackValidForTransfer(stack));
    }

    @Inject(method = "onEntityCollided", at = @At("HEAD"), cancellable = true)
    private static void onEntityCollided(World world, BlockPos pos, BlockState state, Entity entity, HopperBlockEntity blockEntity, CallbackInfo info) {
        GlobalUtils.applyAs(world.getBlockEntity(pos), ChocoHoppers.Handler.class, it -> it.cancelCollide(info));
    }

    @Redirect(method = "getInputItemEntities", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;flatMap(Ljava/util/function/Function;)Ljava/util/stream/Stream;"))
    private static Stream<ItemEntity> offsetMapper(Stream<Box> instance, Function<Box, Stream<ItemEntity>> mapper, World world, Hopper hopper) {
        return instance.flatMap(GlobalUtils.runAsWithDefault(hopper, ChocoHoppers.Handler.class, mapper, it -> it.offsetMapper(world, mapper)));
    }

    @ModifyVariable(method = "extract(Lnet/minecraft/world/World;Lnet/minecraft/block/entity/Hopper;)Z",
        at = @At(value = "STORE", target = "Lnet/minecraft/block/entity/HopperBlockEntity;getInputInventory(Lnet/minecraft/world/World;Lnet/minecraft/block/entity/Hopper;)Lnet/minecraft/inventory/Inventory;"))
    private static Inventory offsetInventoryAt(Inventory inventory, World world, Hopper hopper) {
        return GlobalUtils.runAsWithDefault(hopper, ChocoHoppers.Handler.class, inventory, it -> it.getOffsetInventoryAt(inventory, world));
//        return inventory;
    }

    @Redirect(method = "getInputItemEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/shape/VoxelShape;getBoundingBoxes()Ljava/util/List;"))
    private static List<Box> getInputItemEntities(VoxelShape instance, World world, Hopper hopper) {
        return GlobalUtils.runAsWithDefault(hopper, ChocoHoppers.Handler.class, instance.getBoundingBoxes(), it -> it.extendInputBox(instance));
    }

    @Override
    public Inventory getHopperInventoryAt(World world, double x, double y, double z) {
        return getInventoryAt(world, x, y, z);
    }

    @Inject(method = "readNbt", at = @At("HEAD"))
    public void readNbt(NbtCompound tag, CallbackInfo info) {
        getDictionary().loadFromNbt(tag);
    }

    @Inject(method = "writeNbt", at = @At("HEAD"))
    public void writeNbt(NbtCompound tag, CallbackInfo info) {
        getDictionary().saveToNbt(tag);
    }
}
