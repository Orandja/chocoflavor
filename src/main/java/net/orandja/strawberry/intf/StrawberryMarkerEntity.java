package net.orandja.strawberry.intf;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public interface StrawberryMarkerEntity {

    int getEntityID();

    BlockPos getMiningPos();
    void setMiningPos(BlockPos pos);

    int getTick();
    void setTick(int value);
    default void addTick(int value) {
        this.setTick(this.getTick() + value);
    }

    float getBreakingProgress();
    void setBreakingProgress(float value);

    float getBlockBreakingSpeed();
    void setBlockBreakingSpeed(float value);

    int getLastTick();
    void setLastTick(int value);

    ServerPlayerEntity getPlayer();
    void setPlayer(ServerPlayerEntity value);

}
