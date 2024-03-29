//package net.orandja.chocoflavor.mods.core.mixin;
//
//import net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket;
//import net.minecraft.registry.DynamicRegistryManager;
//import net.minecraft.registry.RegistryKey;
//import net.minecraft.registry.entry.RegistryEntry;
//import net.minecraft.server.MinecraftServer;
//import net.minecraft.server.network.ServerPlayerEntity;
//import net.minecraft.server.world.ServerWorld;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.profiler.Profiler;
//import net.minecraft.world.MutableWorldProperties;
//import net.minecraft.world.StructureWorldAccess;
//import net.minecraft.world.World;
//import net.minecraft.world.dimension.DimensionType;
//import org.spongepowered.asm.mixin.Final;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Shadow;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//import java.util.function.Supplier;
//
//@Mixin(ServerWorld.class)
//abstract class ServerWorldMixin extends World implements StructureWorldAccess {
//
//
//    @Shadow @Final private MinecraftServer server;
//
//    protected ServerWorldMixin(MutableWorldProperties mutableWorldProperties, RegistryKey<World> registryKey, DynamicRegistryManager dynamicRegistryManager, RegistryEntry<DimensionType> registryEntry, Supplier<Profiler> supplier, boolean bl, boolean bl2, long l, int i) {
//        super(mutableWorldProperties, registryKey, dynamicRegistryManager, registryEntry, supplier, bl, bl2, l, i);
//    }
//
//    @Inject(method = "setBlockBreakingInfo", at = @At("HEAD"), cancellable = true)
//    public void setBlockBreakingInfo(int entityId, BlockPos pos, int progress, CallbackInfo info) {
//        for (ServerPlayerEntity serverPlayerEntity : this.server.getPlayerManager().getPlayerList()) {
//            if (serverPlayerEntity != null && serverPlayerEntity.getServerWorld().equals(this)) {
//                double d = (double) pos.getX() - serverPlayerEntity.getX();
//                double e = (double) pos.getY() - serverPlayerEntity.getY();
//                double f = (double) pos.getZ() - serverPlayerEntity.getZ();
//                if (d * d + e * e + f * f < 1024.0) {
//                    serverPlayerEntity.networkHandler.sendPacket(new BlockBreakingProgressS2CPacket(entityId, pos, progress));
//                }
//            }
//        }
//        info.cancel();
//    }
//}