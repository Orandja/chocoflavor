package net.orandja.strawberry.mods.core.intf;

import net.minecraft.block.BlockState;

public interface BlockStateTransformer extends CustomRegistry {
    BlockState transform(BlockState blockState);
}
