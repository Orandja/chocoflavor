package net.orandja.chocoflavor.mixin;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.orandja.chocoflavor.ChocoShulkerBoxes;
import net.orandja.chocoflavor.inventory.CloudInventory;
import net.orandja.chocoflavor.utils.GlobalUtils;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShulkerBoxBlockEntity.class)
abstract class ChocoShulkerBoxes_ShulkerBoxBlockEntityMixin extends LockableContainerBlockEntity implements ChocoShulkerBoxes.CloudHandler {

    protected ChocoShulkerBoxes_ShulkerBoxBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Getter @Setter private CloudInventory cloud;
    @Shadow private DefaultedList<ItemStack> inventory;

    @Override
    public void setBoxInventory(@NotNull DefaultedList<ItemStack> list) {
        this.inventory = list;
    }

    @Override
    public void setName(Text text) {
        this.setCustomName(text);
    }

    @Inject(method = "readNbt", at = @At("HEAD"), cancellable = true)
    void readNbt(NbtCompound tag, CallbackInfo info) {
        readBoxTag(tag, info, super::readNbt);
    }

    @Inject(method = "writeNbt", at = @At("HEAD"), cancellable = true)
    public void writeNbt(NbtCompound tag, CallbackInfo info) {
        writeBoxTag(tag, info, super::writeNbt);
    }
}
