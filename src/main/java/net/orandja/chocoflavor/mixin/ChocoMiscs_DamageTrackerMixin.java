package net.orandja.chocoflavor.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("unused")
@Mixin(DamageTracker.class)
public class ChocoMiscs_DamageTrackerMixin {

    @Final @Shadow private LivingEntity entity;

    @Inject(at = @At("RETURN"), method = "getDeathMessage")
    public void getDeathMessage(CallbackInfoReturnable<Text> info) {
        if(info.getReturnValue() instanceof MutableText text) {
            BlockPos pos = entity.getBlockPos();
            Text.of(" ["+ pos.getX() +";"+ pos.getY() +";"+ pos.getZ() +"] in " + entity.getEntityWorld().getRegistryKey().getValue().toString())
                    .getWithStyle(Style.EMPTY.withColor(TextColor.parse("green")))
                    .forEach(text::append);
        }
    }
}

