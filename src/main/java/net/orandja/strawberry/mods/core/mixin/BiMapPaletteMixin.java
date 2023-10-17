package net.orandja.strawberry.mods.core.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.world.chunk.BiMapPalette;
import net.orandja.strawberry.mods.core.intf.BlockStateTransformer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BiMapPalette.class)
public abstract class BiMapPaletteMixin<T> {

    @Redirect(method = "writePacket", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/collection/IndexedIterable;getRawId(Ljava/lang/Object;)I"))
    public int writeCustomPacket(IndexedIterable instance, T t) {
        if(t instanceof BlockState state) {
            if(state.getBlock() instanceof BlockStateTransformer blockStateTransformer) {
                return instance.getRawId(blockStateTransformer.transform(state));
            }
            if(state.getBlock().equals(Blocks.NOTE_BLOCK)) {
                return instance.getRawId(Blocks.NOTE_BLOCK.getDefaultState());
            }
        }
        return instance.getRawId(t);
    }


    @Redirect(method = "getPacketSize", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/collection/IndexedIterable;getRawId(Ljava/lang/Object;)I"))
    public int getCustomPacketSize(IndexedIterable instance, T t) {
        if(t instanceof BlockState state) {
            if(state.getBlock() instanceof BlockStateTransformer blockStateTransformer) {
                return instance.getRawId(blockStateTransformer.transform(state));
            }
            if(state.getBlock().equals(Blocks.NOTE_BLOCK)) {
                return instance.getRawId(Blocks.NOTE_BLOCK.getDefaultState());
            }
        }
        return instance.getRawId(t);
    }

}
