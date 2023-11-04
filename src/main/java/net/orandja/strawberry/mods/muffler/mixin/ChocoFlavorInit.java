package net.orandja.strawberry.mods.muffler.mixin;

import net.fabricmc.api.ModInitializer;
import net.orandja.strawberry.mods.farming.Farming;
import net.orandja.strawberry.mods.muffler.Muffler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "net.orandja.chocoflavor.ChocoFlavor", remap = false)
public abstract class ChocoFlavorInit implements ModInitializer {

        @SuppressWarnings("UnresolvedMixinReference")
        @Inject(at = @At("RETURN"), method = "beforeLaunch")
        public void initialize(CallbackInfo info) {
                Muffler.beforeLaunch();
        }

}