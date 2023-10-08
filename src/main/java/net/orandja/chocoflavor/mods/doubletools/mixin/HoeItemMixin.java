package net.orandja.chocoflavor.mods.doubletools.mixin;

import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.orandja.chocoflavor.mods.doubletools.DoubleTools;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("unused")
@Mixin(HoeItem.class)
public abstract class HoeItemMixin implements DoubleTools {

    @Inject(cancellable = true, method = "useOnBlock", at = @At("HEAD"))
    public void useOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> info) {
        useHoe(context, info);
    }
}
