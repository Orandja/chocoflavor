//package net.orandja.strawberry.mods.core.mixin;
//
//import net.fabricmc.api.ModInitializer;
//import net.orandja.strawberry.mods.core.CustomItemsAndBlocks;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Pseudo;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//@Pseudo
//@Mixin(targets = "net.orandja.chocoflavor.ChocoFlavor", remap = false)
//public abstract class ChocoFlavorInit implements ModInitializer {
//
//        @SuppressWarnings("UnresolvedMixinReference")
//        @Inject(at = @At("RETURN"), method = "duringInit")
//        public void initialize(CallbackInfo info) {
//                CustomItemsAndBlocks.beforeLaunch();
//        }
//
//}