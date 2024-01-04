package net.orandja.chocoflavor.mixin;

import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.orandja.chocoflavor.ChocoDoubleTools;
import net.orandja.chocoflavor.ChocoTools;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HoeItem.class)
public abstract class ChocoDoubleTools_HoeItemMixin implements ChocoDoubleTools.HoeHandler {
    @Inject(cancellable = true, method = "useOnBlock", at = @At("HEAD"))
    public void useOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> info) {
        useDoubleHoe(context, info);
    }
}
