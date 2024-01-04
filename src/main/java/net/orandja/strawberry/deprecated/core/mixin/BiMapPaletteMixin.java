//package net.orandja.strawberry.mods.core.mixin;
//
//import net.minecraft.block.BlockState;
//import net.minecraft.util.collection.IndexedIterable;
//import net.minecraft.world.chunk.BiMapPalette;
//import net.orandja.strawberry.StrawberryCustomBlocks;
//import net.orandja.strawberry.mods.core.CustomItemsAndBlocks;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Redirect;
//
//@Mixin(BiMapPalette.class)
//public abstract class BiMapPaletteMixin<T> implements StrawberryCustomBlocks.BlockStateHandler {
//
//    @Redirect(method = "writePacket", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/collection/IndexedIterable;getRawId(Ljava/lang/Object;)I"))
//    public int writeCustomPacket(IndexedIterable instance, T t) {
//        if(t instanceof BlockState state) {
//            return CustomItemsAndBlocks.interceptBlockState(state, instance::getRawId, instance.getRawId(t));
//        }
//        return instance.getRawId(t);
//    }
//
//
//    @Redirect(method = "getPacketSize", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/collection/IndexedIterable;getRawId(Ljava/lang/Object;)I"))
//    public int getCustomPacketSize(IndexedIterable instance, T t) {
//        if(t instanceof BlockState state) {
//            return CustomItemsAndBlocks.interceptBlockState(state, instance::getRawId, instance.getRawId(t));
//        }
//        return instance.getRawId(t);
//    }
//
//}
