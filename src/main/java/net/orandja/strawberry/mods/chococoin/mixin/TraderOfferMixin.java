package net.orandja.strawberry.mods.chococoin.mixin;

import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.village.TradeOffer;
import net.orandja.chocoflavor.utils.GlobalUtils;
import net.orandja.strawberry.mods.chococoin.ChocoCoin;
import net.orandja.strawberry.mods.chococoin.item.CoinItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static net.orandja.strawberry.mods.chococoin.ChocoCoin.*;

@Mixin(TradeOffer.class)
public abstract class TraderOfferMixin implements ChocoCoin.Handler {



    @Mutable @Shadow @Final private ItemStack firstBuyItem;
    @Mutable @Shadow @Final private ItemStack secondBuyItem;
    @Mutable @Shadow @Final private ItemStack sellItem;

    private ItemStack firstBuyItemBackup;
    private ItemStack secondBuyItemBackup;
    @Getter
    private ItemStack sellItemBackup;

    @Shadow private int uses;
    @Shadow @Final private int maxUses;
    @Shadow private boolean rewardingPlayerExperience;
    @Shadow private int merchantExperience;
    @Shadow private float priceMultiplier;
    @Shadow private int specialPrice;
    @Shadow private int demandBonus;

    @Shadow protected abstract boolean acceptsBuy(ItemStack given, ItemStack sample);

    @Shadow public abstract ItemStack getAdjustedFirstBuyItem();

    @Shadow public abstract ItemStack getSecondBuyItem();

    @Shadow public abstract boolean matchesBuyItems(ItemStack first, ItemStack second);

    @Shadow public abstract ItemStack getOriginalFirstBuyItem();

    @Getter private int chocoUnit;

    @Inject(method = "<init>(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;IIIF)V", at = @At("TAIL"))
    public void onInit(ItemStack firstBuyItem, ItemStack secondBuyItem, ItemStack sellItem, int _uses, int maxUses, int merchantExperience, float priceMultiplier, CallbackInfo info) {
        initChocoTrade();
    }

    @Inject(method = "<init>(Lnet/minecraft/nbt/NbtCompound;)V", at = @At("TAIL"))
    public void onInit(NbtCompound nbt, CallbackInfo info) {
        initChocoTrade();
    }

    private void initChocoTrade() {
        this.firstBuyItemBackup = this.firstBuyItem;
        this.secondBuyItemBackup = this.secondBuyItem;
        this.sellItemBackup = this.sellItem;

        chocoUnit = CoinUtils.getChocoUnit(this.firstBuyItem, this.secondBuyItem);
//        if(chocoUnit > 0) {
//            List<ItemStack> stacks = CoinUtils.convertToStacks(chocoUnit);
//            for (int i = 0; i < stacks.size() && i < 2; i++) {
//                if(i == 0) this.firstBuyItem = stacks.get(0);
//                if(i == 1 && (this.secondBuyItem.isEmpty() || this.secondBuyItem.isOf(Items.EMERALD))) this.secondBuyItem = stacks.get(1);
//            }
//        }

        if(this.sellItem.getItem() == Items.EMERALD) {
            int sellChocoUnit = CoinUtils.getChocoUnit(this.sellItem);
            if(sellChocoUnit > 0) {
                this.sellItem = CoinUtils.convertToStacks(sellChocoUnit).get(0);
            }
        }
    }

    @Inject(method = "toNbt", at = @At("HEAD"), cancellable = true)
    public void toNbt(CallbackInfoReturnable<NbtCompound> info) {
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.put("buy", this.firstBuyItemBackup.writeNbt(new NbtCompound()));
        nbtCompound.put("sell", this.sellItemBackup.writeNbt(new NbtCompound()));
        nbtCompound.put("buyB", this.secondBuyItemBackup.writeNbt(new NbtCompound()));
        nbtCompound.putInt("uses", this.uses);
        nbtCompound.putInt("maxUses", this.maxUses);
        nbtCompound.putBoolean("rewardExp", this.rewardingPlayerExperience);
        nbtCompound.putInt("xp", this.merchantExperience);
        nbtCompound.putFloat("priceMultiplier", this.priceMultiplier);
        nbtCompound.putInt("specialPrice", this.specialPrice);
        nbtCompound.putInt("demand", this.demandBonus);
        info.setReturnValue(nbtCompound);
    }

    @Inject(method = "matchesBuyItems", at = @At("HEAD"), cancellable = true)
    public void matchesBuyItems(ItemStack first, ItemStack second, CallbackInfoReturnable<Boolean> info) {
        if(chocoUnit > 0) {
            int availableChocoUnits = CoinUtils.getChocoUnit(first, second);
            int returningChocoUnits = availableChocoUnits - chocoUnit;
            List<ItemStack> returningStacks = CoinUtils.convertToStacks(returningChocoUnits);
            int availableSlots = GlobalUtils.count(wouldBeAvailable(first, this.firstBuyItem), wouldBeAvailable(second, this.secondBuyItem));

            info.setReturnValue(returningChocoUnits >= 0 && returningStacks.size() <= availableSlots);
        }
    }

    public boolean wouldBeAvailable(ItemStack slotStack, ItemStack tradeStack) {
        if(slotStack.getItem() instanceof CoinItem coin || slotStack.isEmpty()) {
            return true;
        }

        return this.acceptsBuy(slotStack, tradeStack) && (slotStack.getCount() - tradeStack.getCount()) <= 0;
    }

    @Inject(method = "depleteBuyItems", at = @At("HEAD"), cancellable = true)
    public void depleteBuyItems(ItemStack firstBuyStack, ItemStack secondBuyStack, CallbackInfoReturnable<Boolean> info) {
        if(chocoUnit > 0) {
            if (!this.matchesBuyItems(firstBuyStack, secondBuyStack)) {
                info.setReturnValue(false);
                return;
            }
            if(!(firstBuyStack.getItem() instanceof CoinItem)) {
                firstBuyStack.decrement(this.getAdjustedFirstBuyItem().getCount());
            }
            if (!this.getSecondBuyItem().isEmpty() && !(secondBuyStack.getItem() instanceof CoinItem)) {
                secondBuyStack.decrement(this.getSecondBuyItem().getCount());
            }
            info.setReturnValue(false);
        }
    }
}
