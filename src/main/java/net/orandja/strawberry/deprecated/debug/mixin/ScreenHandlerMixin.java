//package net.orandja.strawberry.mods.debug.mixin;
//
//import net.minecraft.item.ItemStack;
//import net.minecraft.screen.ScreenHandler;
//import net.minecraft.screen.ScreenHandlerListener;
//import org.spongepowered.asm.mixin.Final;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Shadow;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//import java.util.List;
//import java.util.function.Supplier;
//
//@Mixin(ScreenHandler.class)
//public abstract class ScreenHandlerMixin {
//
//    @Shadow @Final private List<ScreenHandlerListener> listeners;
//
//    @Inject(method = "updateTrackedSlot", at = @At(value = "INVOKE", target = "Ljava/util/function/Supplier;get()Ljava/lang/Object;", shift = At.Shift.AFTER))
//    private void updateTrackedSlot(int slot, ItemStack stack, Supplier<ItemStack> copySupplier, CallbackInfo ci) {
////        Utils.log("SENDING UPDATE", slot, stack);
//    }
//
//}
