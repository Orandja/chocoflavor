package net.orandja.strawberry.mods.core.mixin;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.ChunkData;
import net.orandja.chocoflavor.ChocoFlavor;
import net.orandja.strawberry.mods.core.intf.CustomBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collection;
import java.util.stream.Collectors;

@Mixin(ChunkData.class)
public abstract class ChunkDataMixin {

    @Redirect(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketByteBuf;writeCollection(Ljava/util/Collection;Lnet/minecraft/network/PacketByteBuf$PacketWriter;)V"))
    public <T> void writeThatCollection(PacketByteBuf instance, Collection<T> collection, PacketByteBuf.PacketWriter<T> writer) {
        instance.writeCollection(collection.stream().filter(it -> {
            if(it instanceof CustomBlockEntity) {
                return false;
            }

            return true;
        }).collect(Collectors.toList()), writer);
    }
}
