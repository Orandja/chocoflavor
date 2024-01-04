package net.orandja.strawberry.mods.chococoin.mixin;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.village.Merchant;
import net.minecraft.village.MerchantInventory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MerchantInventory.class)
public abstract class MerchantInventoryMixin
        implements Inventory {

    @Shadow
    @Final
    private Merchant merchant;

    @Shadow
    public abstract ItemStack getStack(int slot);

    @Shadow @Final private DefaultedList<ItemStack> inventory;

//    @Inject(method = "<init>", at = @At("TAIL"))
//    public void justDoIt(Merchant merchant, CallbackInfo ci) {
//        this.inventory.set(2, new ItemStack(Items.ACACIA_LEAVES, 64));
//    }
//
//    @Inject(method = "updateOffers", at = @At(value = "INVOKE", target = "Lnet/minecraft/village/MerchantInventory;setStack(ILnet/minecraft/item/ItemStack;)V", ordinal = 1))
//    public void updateOffers(CallbackInfo info) {
//        if (this.merchant instanceof CoinMerchantEntity coinMerchant) {
//            coinMerchant.resend(() -> {
//                if (this.merchant.getCustomer() instanceof ServerPlayerEntity player) {
//                    player.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(player.currentScreenHandler.syncId, player.currentScreenHandler.nextRevision(), 2, this.getStack(2).copy()));
//                }
//            });
//        }
//    }

}
