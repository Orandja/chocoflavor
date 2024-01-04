//package net.orandja.chocoflavor.mods.blockswithenchantments.mixin;
//
//import net.fabricmc.api.ModInitializer;
//import net.orandja.chocoflavor.mods.blockswithenchantments.BrewingStand;
//import net.orandja.chocoflavor.mods.blockswithenchantments.DeepStorageBarrel;
//import net.orandja.chocoflavor.mods.blockswithenchantments.FurnaceWithEnchantment;
//import net.orandja.chocoflavor.mods.blockswithenchantments.HopperWithEnchantment;
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
//                BrewingStand.beforeLaunch();
//                DeepStorageBarrel.beforeLaunch();
//                FurnaceWithEnchantment.beforeLaunch();
//                HopperWithEnchantment.beforeLaunch();
//        }
//
//}