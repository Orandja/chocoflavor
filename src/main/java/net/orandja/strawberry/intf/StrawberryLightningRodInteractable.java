package net.orandja.strawberry.intf;

import net.minecraft.entity.LightningEntity;
import net.minecraft.util.math.BlockPos;

public interface StrawberryLightningRodInteractable {

    void onLightningInteract(LightningEntity it, BlockPos down);

}
