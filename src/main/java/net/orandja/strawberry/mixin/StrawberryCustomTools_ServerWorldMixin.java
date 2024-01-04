package net.orandja.strawberry.mixin;

import net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ServerWorld.class)
abstract class StrawberryCustomTools_ServerWorldMixin implements StructureWorldAccess {

    @Shadow
    @Final
    private MinecraftServer server;

    @Inject(method = "setBlockBreakingInfo", at = @At("HEAD"), cancellable = true)
    public void setBlockBreakingInfo(int entityId, BlockPos pos, int progress, CallbackInfo info) {
        for (ServerPlayerEntity serverPlayerEntity : this.server.getPlayerManager().getPlayerList()) {
            if (serverPlayerEntity != null && serverPlayerEntity.getServerWorld().equals(this)) {
                double d = (double) pos.getX() - serverPlayerEntity.getX();
                double e = (double) pos.getY() - serverPlayerEntity.getY();
                double f = (double) pos.getZ() - serverPlayerEntity.getZ();
                if (d * d + e * e + f * f < 1024.0) {
                    serverPlayerEntity.networkHandler.sendPacket(new BlockBreakingProgressS2CPacket(entityId, pos, progress));
                }
            }
        }
        info.cancel();
    }
}