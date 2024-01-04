package net.orandja.strawberry.mixin;

import net.fabricmc.api.ModInitializer;
import net.orandja.strawberry.StrawberryCustomBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "net.orandja.chocoflavor.ChocoFlavor", remap = false)
public abstract class StrawberryCustomBlocks_InitMixin implements ModInitializer {

        @SuppressWarnings("UnresolvedMixinReference")
        @Inject(at = @At("RETURN"), method = "duringInit")
        public void initialize(CallbackInfo info) {
                StrawberryCustomBlocks.init();
        }

}