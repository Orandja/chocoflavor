package net.orandja.strawberry.mods.core.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.world.chunk.ArrayPalette;
import net.orandja.strawberry.mods.core.intf.StrawberryBlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ArrayPalette.class)
public class ArrayPaletteMixin<T> {


//    @Shadow @Final private T[] array;
//
//    @Shadow private int size;
//
//    @Shadow @Final private IndexedIterable<T> idList;
//
//    @Inject(method = "writePacket", at = @At("HEAD"), cancellable = true)
//    public void writeCustomPacket(PacketByteBuf buf, CallbackInfo info) {
//        buf.writeVarInt(this.size);
//        for (int i = 0; i < this.size; ++i) {
//            buf.writeVarInt(this.idList.getRawId(this.array[i]));
//        }
//        info.cancel();
//        for (int i = 0; i < this.size; ++i) {
//            ChocoFlavor.LOGGER.info("["+ i +"] " + (i < this.array.length ? this.array[i] : " OUT OF BOUND "));
//        }
//
//        if(this.idList.get(24308) != null) {
//            ChocoFlavor.LOGGER.info(this.idList.get(24308));
//        }
//    }
//
//    @Inject(method = "getCustomPacketSize", at = @At("HEAD"), cancellable = true)
//    public void getCustomPacketSize() {
//        int i = VarInts.getSizeInBytes(this.getSize());
//        for (int j = 0; j < this.getSize(); ++j) {
//            i += VarInts.getSizeInBytes(this.idList.getRawId(this.array[j]));
//        }
//        return i;
//    }


    @Redirect(method = "writePacket", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/collection/IndexedIterable;getRawId(Ljava/lang/Object;)I"))
    public int writeCustomPacket(IndexedIterable instance, T t) {
        if(t instanceof BlockState state) {
            if(state.getBlock() instanceof StrawberryBlockState blockStateTransformer) {
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
            if(state.getBlock() instanceof StrawberryBlockState blockStateTransformer) {
                return instance.getRawId(blockStateTransformer.transform(state));
            }
            if(state.getBlock().equals(Blocks.NOTE_BLOCK)) {
                return instance.getRawId(Blocks.NOTE_BLOCK.getDefaultState());
            }
        }
        return instance.getRawId(t);
    }

//    @Override
//    public void writePacket(PacketByteBuf buf) {
//        buf.writeVarInt(this.size);
//        for (int i = 0; i < this.size; ++i) {
//            buf.writeVarInt(this.idList.getRawId(this.array[i]));
//        }
//    }
//
//    @Override
//    public int getPacketSize() {
//        int i = VarInts.getSizeInBytes(this.getSize());
//        for (int j = 0; j < this.getSize(); ++j) {
//            i += VarInts.getSizeInBytes(this.idList.getRawId(this.array[j]));
//        }
//        return i;
//    }
}
