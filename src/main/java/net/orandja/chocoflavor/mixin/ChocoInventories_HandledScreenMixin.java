package net.orandja.chocoflavor.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.orandja.chocoflavor.ChocoInventories;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenHandler.class)
public abstract class ChocoInventories_HandledScreenMixin<T extends ScreenHandler> implements ChocoInventories.MiddleClickHandler {

    @Shadow
    private ItemStack cursorStack;

    @Inject(method = "onSlotClick", at = @At("HEAD"))
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo info) {
        checkMiddleClick(cursorStack, actionType, player);
    }
}