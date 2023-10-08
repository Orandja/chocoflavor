package net.orandja.chocoflavor.mods.deepstoragebarrel.mixin;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.orandja.chocoflavor.mods.deepstoragebarrel.DeepStorageBarrel;
import net.orandja.chocoflavor.mods.core.BlockWithEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("unused")
@Mixin(BarrelBlockEntity.class)
public abstract class BarrelBlockEntityMixin extends LootableContainerBlockEntity implements DeepStorageBarrel {

    @Shadow @Getter @Setter private DefaultedList<ItemStack> inventory;
    @Getter EnchantmentDictionary enchantmentDictionary = new EnchantmentDictionary(VALID_ENCHANTMENTS);

    protected BarrelBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Inject(method = "readNbt", at = @At("HEAD"), cancellable = true)
    public void readNbt(NbtCompound tag, CallbackInfo info) {
        this.loadEnchantments(tag, info, super::readNbt);
    }

    @Inject(method = "writeNbt", at = @At("HEAD"), cancellable = true)
    public void writeNbt(NbtCompound tag, CallbackInfo info) {
        this.saveEnchantments(tag, info, super::writeNbt);
    }

    @Inject(method = "size", cancellable = true, at = @At("HEAD"))
    public void size(CallbackInfoReturnable<Integer> info) {
        getBarrelSize(info);
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return isValidForBarrel(slot, stack);
    }

    @Inject(method = "createScreenHandler", at = @At("HEAD"), cancellable = true)
    protected void createScreenHandler(int syncId, PlayerInventory playerInventory, CallbackInfoReturnable<ScreenHandler> info) {
        this.createBarrelScreenHandler(this, syncId, playerInventory, info);
    }

//    public void applyEnchantments() {
//        this.setInventory(DeepStorageDefaultedList.ofSize(27 + this.getEnchantmentDictionary().computeValue(it -> it * 27, VALID_ENCHANTMENTS), 27, ItemStack.EMPTY));
//    }

    @Override
    public void markDirty() {
        this.onMarkDirty();
        super.markDirty();
    }
}
