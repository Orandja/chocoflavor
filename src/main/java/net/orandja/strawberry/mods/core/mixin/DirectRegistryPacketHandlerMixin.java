package net.orandja.strawberry.mods.core.mixin;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.fabricmc.fabric.impl.registry.sync.packet.DirectRegistryPacketHandler;
import net.fabricmc.fabric.mixin.lookup.BlockEntityTypeAccessor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.network.packet.Packet;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.orandja.chocoflavor.ChocoFlavor;
import net.orandja.strawberry.mods.core.intf.CustomRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

// Also need to hack into Fabric to stop the fabric-api from sending unknown registry keys...
//@Pseudo()
@Mixin(value = DirectRegistryPacketHandler.class, remap = false)
public class DirectRegistryPacketHandlerMixin {

    @Inject(method = "sendPacket", at = @At("HEAD"))
    public void hackToPacket(Consumer<Packet<?>> sender, Map<Identifier, Object2IntMap<Identifier>> registryMap, CallbackInfo info) {
        registryMap.forEach((registryId, map) -> {
            Registry registry = Registries.REGISTRIES.get(registryId);
            List<Identifier> toRemove = new ArrayList();
            map.forEach((id, rawID) -> {
                if(registry.get(id) instanceof CustomRegistry) {
                    toRemove.add(id);
                }

                if(registry.get(id) instanceof BlockEntityTypeAccessor blockEntityType) {
                    blockEntityType.getBlocks().stream().filter(it -> it instanceof CustomRegistry).findAny().ifPresent(it -> {
                        toRemove.add(id);
                    });
                }
            });
            toRemove.forEach(map::removeInt);
        });
    }

}
