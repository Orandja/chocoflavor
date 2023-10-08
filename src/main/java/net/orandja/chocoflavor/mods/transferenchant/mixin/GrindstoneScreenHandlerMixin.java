package net.orandja.chocoflavor.mods.transferenchant.mixin;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.GrindstoneScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.orandja.chocoflavor.mods.transferenchant.TransferEnchant;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GrindstoneScreenHandler.class)
public abstract class GrindstoneScreenHandlerMixin extends ScreenHandler implements TransferEnchant {
    protected GrindstoneScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }

    @Final @Shadow @Getter Inventory input;
    @Final @Shadow @Getter private Inventory result;
    @Unique @Getter TransferEnchantOutputSlot outputSlot;

    @Override
    public TransferEnchantOutputSlot vw$setOutputSlot(TransferEnchantOutputSlot outputSlot) {
        this.outputSlot = outputSlot;
        return outputSlot;
    }

    @Unique @Getter @Setter PlayerInventory playerInventory;
    @Final @Getter @Shadow private ScreenHandlerContext context;

    @Inject(at = @At("RETURN"), method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/screen/ScreenHandlerContext;)V")
    public void init(int syncId, PlayerInventory playerInventory, final ScreenHandlerContext context, CallbackInfo info) {
        onInit(playerInventory, context);
    }

    @Inject(at = @At("HEAD"), method = "updateResult", cancellable = true)
    public void updateResult(CallbackInfo info) {
        updateTransferResult(info);
    }

    @Override
    public void vw$updateContents() {
        this.sendContentUpdates();
    }

    @Override
    public void vw$replaceSlot(Slot slot, int id) {
        slot.id = id;
        this.slots.set(id, slot);
    }
}
