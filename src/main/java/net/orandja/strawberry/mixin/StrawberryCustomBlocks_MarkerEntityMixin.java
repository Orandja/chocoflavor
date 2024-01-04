package net.orandja.strawberry.mixin;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MarkerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.orandja.strawberry.intf.StrawberryMarkerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MarkerEntity.class)
public abstract class StrawberryCustomBlocks_MarkerEntityMixin extends Entity implements StrawberryMarkerEntity {

    @Getter @Setter private BlockPos miningPos;
    @Getter @Setter private int tick = -2;
    @Getter @Setter private float breakingProgress;
    @Getter @Setter private int lastTick = -2;
    @Getter @Setter private ServerPlayerEntity player;
    @Getter @Setter private float blockBreakingSpeed;

    @Override
    public int getEntityID() {
        return this.getId();
    }

    public StrawberryCustomBlocks_MarkerEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void onTick(CallbackInfo info) {
        if(player == null || player.isDisconnected()) {
            this.kill();
            return;
        }

        if(tick > -1)
        if (tick != -2 && tick == lastTick) {
            tick = -1;
            lastTick = -1;
            breakingProgress = -1f;
            if(miningPos != null) {
                getWorld().setBlockBreakingInfo(getId(), miningPos, -1);
                miningPos = null;
            }
        }

        lastTick = tick;
    }
}
