package net.orandja.strawberry.intf;

import net.minecraft.block.BlockState;

public interface StrawberryBlockState extends StrawberryObject {
    BlockState transform(BlockState blockState);
}
