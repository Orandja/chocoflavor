package net.orandja.strawberry.mods.chococoin.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.TradeOutputSlot;
import net.minecraft.stat.Stats;
import net.minecraft.village.Merchant;
import net.minecraft.village.MerchantInventory;
import net.minecraft.village.TradeOffer;
import net.orandja.strawberry.mods.chococoin.ChocoCoin;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(TradeOutputSlot.class)
public abstract class TradeOutputSlotMixin extends Slot {

    @Shadow @Final private MerchantInventory merchantInventory;

    @Shadow @Final private Merchant merchant;

    public TradeOutputSlotMixin(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Inject(method = "onTakeItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/village/Merchant;setExperienceFromServer(I)V", shift = At.Shift.BEFORE))
    public void depleteCoins(PlayerEntity player, ItemStack stack, CallbackInfo info) {
        TradeOffer tradeOffer = this.merchantInventory.getTradeOffer();
        if (tradeOffer instanceof ChocoCoin.Handler handler) {
            int chocoUnit = handler.getChocoUnit();
            if(chocoUnit > 0) {
                ItemStack first = this.merchantInventory.getStack(0);
                ItemStack second = this.merchantInventory.getStack(1);

                this.merchant.trade(tradeOffer);
                player.incrementStat(Stats.TRADED_WITH_VILLAGER);

                int availableChocoUnits = ChocoCoin.CoinUtils.getChocoUnit(first, second);
                int returningChocoUnits = availableChocoUnits - chocoUnit;
                List<ItemStack> returningStacks = ChocoCoin.CoinUtils.convertToStacks(returningChocoUnits);

                for (int i = 0; i < returningStacks.size(); i++) {
                    if(i == 0) first = returningStacks.get(0);
                    if(i == 1) second = returningStacks.get(1);
                }

                this.merchantInventory.setStack(0, first);
                this.merchantInventory.setStack(1, second);
//                Utils.log("RETURNING CHANGE", returnedCoins, chocoUnit, availableValue, returnedValue);
//                if(returnedCoins.length() <= DESC_COINS.length) {
//                    for(int i = 0; i < 2 && i < returnedCoins.length(); i++) {
//                        long count = Long.parseLong(returnedCoins.substring(i, i + 1));
//                        if(count > 0)
//                            this.merchantInventory.setStack(i, new ItemStack(ChocoCoin.DESC_COINS[i], (int)count));
//                    }
//                } else {
//                    long count = Long.parseLong(returnedCoins.substring(0, 2), 9) / ChocoCoin.DESC_COINS[0].getEmeraldValue();
//                    this.merchantInventory.setStack(0, new ItemStack(ChocoCoin.DESC_COINS[0], (int)count));
//                    for(int i = 0; i < 2; i++) {
//                        count = Long.parseLong(returnedCoins.substring(2 + i, 3 + i));
//                        if(count > 0) {
//                            this.merchantInventory.setStack(1, new ItemStack(ChocoCoin.DESC_COINS[i + 1], (int)count));
//                            break;
//                        }
//                    }
//                }
            }
        }
    }

}
