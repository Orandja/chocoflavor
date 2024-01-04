package net.orandja.strawberry.mods.chococoin.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.village.Merchant;
import net.minecraft.village.MerchantInventory;
import net.orandja.chocoflavor.accessor.ScreenHandlerAccessor;
import net.orandja.strawberry.mods.chococoin.CoinMerchantEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MerchantScreenHandler.class)
public abstract class MerchantScreenHandlerMixin extends ScreenHandler {

    @Shadow @Final private MerchantInventory merchantInventory;

    @Shadow @Final private Merchant merchant;

    protected MerchantScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        super.onSlotClick(slotIndex, button, actionType, player);
        if (this.merchant instanceof CoinMerchantEntity coinMerchant) {
            coinMerchant.resend(() -> {
                if(this instanceof ScreenHandlerAccessor accessor) {
                    for (ScreenHandlerListener listener : accessor.getListeners()) {
                        listener.onSlotUpdate(this, 2, this.merchantInventory.getStack(2).copy());
                    }
                }
            });
        }
//        if(this instanceof ScreenHandlerAccessor accessor) {
//            Utils.log(this.merchantInventory.getStack(2).copy(), this.getSlot(2).getStack().copy());
//            for (ScreenHandlerListener listener : accessor.getListeners()) {
//                listener.onSlotUpdate(this, 2, this.merchantInventory.getStack(2).copy());
//            }
//        }
//        if (this.merchant.getCustomer() instanceof ServerPlayerEntity player) {
//            player.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(player.currentScreenHandler.syncId, player.currentScreenHandler.nextRevision(), 2, this.getStack(2).copy()));
//        }
    }

}
