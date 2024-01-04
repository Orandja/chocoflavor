package net.orandja.strawberry.mixin;

import com.mojang.authlib.GameProfile;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MarkerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.orandja.strawberry.intf.StrawberryMarkerEntity;
import net.orandja.strawberry.intf.StrawberryPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("LombokGetterMayBeUsed")
@Mixin(ServerPlayerEntity.class)
public abstract class StrawberryCustomBlocks_ServerPlayerEntityMixin extends PlayerEntity implements StrawberryPlayer {

    @Shadow public abstract ServerWorld getServerWorld();

    @Getter @Setter private MarkerEntity breakerEntity;

    public StrawberryCustomBlocks_ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void onTick(CallbackInfo info) {
        if(breakerEntity != null && breakerEntity.isAlive()) {
            breakerEntity.setPosition(getPos());
            return;
        }

        breakerEntity = new MarkerEntity(EntityType.MARKER, getWorld());
        //noinspection ConstantValue
        if(breakerEntity instanceof StrawberryMarkerEntity strawberryMarkerEntity) {
            strawberryMarkerEntity.setPlayer(ServerPlayerEntity.class.cast(this));
        }
        breakerEntity.setPosition(this.getPos());
        getWorld().spawnEntity(breakerEntity);
    }
}
