package net.orandja.strawberry.mods.core.mixin;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.util.collection.IndexedIterable;
import net.orandja.strawberry.mods.core.intf.StrawberryBlockState;
import net.orandja.strawberry.mods.core.intf.StrawberryItem;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PacketByteBuf.class)
public abstract class PacketByteBufMixin {

    @Shadow public abstract PacketByteBuf writeBoolean(boolean bl);

    @Shadow public abstract <T> void writeRegistryValue(IndexedIterable<T> registry, T value);

    @Shadow public abstract ByteBuf writeByte(int value);

    @Shadow public abstract PacketByteBuf writeNbt(@Nullable NbtElement nbt);

    @Shadow public abstract PacketByteBuf writeVarInt(int value);

    @Inject(method = "writeItemStack", at = @At("HEAD"), cancellable = true)
    public void writeCustomItemStack(ItemStack stack, CallbackInfoReturnable<PacketByteBuf> info) {
        if(stack.getItem() instanceof StrawberryItem itemStackTransformer) {
            ItemStack customStack = itemStackTransformer.transform(stack);
            this.writeBoolean(true);
            Item item = customStack.getItem();
            this.writeRegistryValue(Registries.ITEM, item);
            this.writeByte(customStack.getCount());
            NbtCompound nbtCompound = null;
            if (item.isDamageable() || item.isNbtSynced()) {
                nbtCompound = customStack.getNbt();
            }
            this.writeNbt(nbtCompound);

            info.cancel();
        }
    }

    @Inject(method = "writeRegistryValue", at = @At("HEAD"), cancellable = true)
    public <T> void writeCustomBlockState(IndexedIterable<T> registry, T value, CallbackInfo info) {
        if(value instanceof BlockState state && state.getBlock() instanceof StrawberryBlockState blockStateTransformer) {
            int i = registry.getRawId((T) blockStateTransformer.transform(state));
            if (i == -1) {
                throw new IllegalArgumentException("Can't find id for '" + value + "' in map " + registry);
            }
            this.writeVarInt(i);
            info.cancel();
        }
    }

}
