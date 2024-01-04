package net.orandja.chocoflavor.mixin;

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
import net.orandja.chocoflavor.ChocoBarrels;
import net.orandja.chocoflavor.enchantment.EnchantmentDictionary;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BarrelBlockEntity.class)
public abstract class ChocoBarrels_BarrelBlockEntityMixin extends LootableContainerBlockEntity implements ChocoBarrels.Handler {

    @Shadow @Getter @Setter private DefaultedList<ItemStack> inventory;
    protected @Unique @Getter EnchantmentDictionary dictionary = createDictionary();

    protected ChocoBarrels_BarrelBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
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

    @Override
    public void setStack(int slot, ItemStack stack) {
        setStack(slot, stack, super::setStack);
    }

    @Inject(method = "readNbt", at = @At("HEAD"), cancellable = true)
    public void readNbt(NbtCompound tag, CallbackInfo info) {
        this.loadEnchantments(tag, info, super::readNbt);
    }

    @Inject(method = "writeNbt", at = @At("HEAD"), cancellable = true)
    public void writeNbt(NbtCompound tag, CallbackInfo info) {
        this.saveEnchantments(tag, info, super::writeNbt);
    }

}
