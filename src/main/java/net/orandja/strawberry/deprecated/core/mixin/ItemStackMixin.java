//package net.orandja.strawberry.mods.core.mixin;
//
//
//import net.minecraft.item.Item;
//import net.minecraft.item.ItemStack;
//import net.orandja.strawberry.intf.ItemStackAccessor;
//import org.jetbrains.annotations.Nullable;
//import org.spongepowered.asm.mixin.Final;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Mutable;
//import org.spongepowered.asm.mixin.Shadow;
//
//@Mixin(ItemStack.class)
//public abstract class ItemStackMixin implements ItemStackAccessor {
//
//    @Mutable @Shadow @Final @Deprecated private @Nullable Item item;
//
//    @Override
//    public void setItem(Item item) {
//        this.item = item;
//    }
//}
//
