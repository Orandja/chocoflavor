package net.orandja.chocoflavor.mods.deathmessage.mixin;

import lombok.Getter;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.text.Text;
import net.orandja.chocoflavor.mods.deathmessage.DeathMessage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("unused")
@Mixin(DamageTracker.class)
public class DamageTrackerMixin implements DeathMessage {

    @Final @Shadow @Getter private LivingEntity entity;

    @Inject(at = @At("RETURN"), method = "getDeathMessage", cancellable = true)
    public void getDeathMessage(CallbackInfoReturnable<Text> info) {
        sendDeathPosition(info);
    }
}
