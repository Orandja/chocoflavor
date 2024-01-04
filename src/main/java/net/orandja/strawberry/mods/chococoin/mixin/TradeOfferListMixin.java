package net.orandja.strawberry.mods.chococoin.mixin;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.orandja.strawberry.mods.chococoin.ChocoCoin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;

@Mixin(TradeOfferList.class)
public abstract class TradeOfferListMixin extends ArrayList<TradeOffer> {

    int lastIndex = 0;

    @Override
    public boolean add(TradeOffer tradeOffer) {
        if(tradeOffer instanceof ChocoCoin.Handler handler && handler.getChocoUnit() > 0) {
            ChocoCoin.CoinUtils.possibleStacks(handler.getChocoUnit()).forEach(subList -> {
                if(subList.size() == 2) {
                    super.add(this.size(), new TradeOffer(subList.get(1), subList.get(0), handler.getSellItemBackup(), tradeOffer.getMaxUses(), tradeOffer.getMerchantExperience(), tradeOffer.getPriceMultiplier()));
                } else if(subList.size() == 1) {
                    super.add(this.size(), new TradeOffer(subList.get(0), handler.getSellItemBackup(), tradeOffer.getMaxUses(), tradeOffer.getMerchantExperience(), tradeOffer.getPriceMultiplier()));
                }
            });
        } else {
            super.add(lastIndex, tradeOffer);
        }
        lastIndex++;
        return true;
    }

    @Inject(method = "toNbt", at = @At("HEAD"), cancellable = true)
    public void toNBTLimited(CallbackInfoReturnable<NbtCompound> info) {
        NbtCompound nbtCompound = new NbtCompound();
        NbtList nbtList = new NbtList();
        for (int i = 0; i < lastIndex; ++i) {
            TradeOffer tradeOffer = this.get(i);
            nbtList.add(tradeOffer.toNbt());
        }
        nbtCompound.put("Recipes", nbtList);
        info.setReturnValue(nbtCompound);
//        ChocoCoin.CoinUtils.possibleStacks(chocoUnit).forEach(Utils::logAll);
    }

}
