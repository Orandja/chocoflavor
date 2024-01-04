package net.orandja.chocoflavor.mixin;

import net.orandja.chocoflavor.ChocoHoppers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "net.orandja.chocoflavor.ChocoFlavor", remap = false)
public abstract class ChocoHoppers_InitMixin {

        @SuppressWarnings("UnresolvedMixinReference")
        @Inject(at = @At("RETURN"), method = "duringInit")
        public void initialize(CallbackInfo info) {
            ChocoHoppers.init();
        }


}
