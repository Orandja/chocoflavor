package net.orandja.strawberry.mixin;

import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerChunkManager.class)
public abstract class StrawberryCustomBlocks_ServerChunkManagerMixin {

    @Shadow public abstract BlockView getWorld();

    @Inject(method = "markForUpdate", at = @At("HEAD"), cancellable = true)
    public void interceptMarkForUpdate(BlockPos pos, CallbackInfo info) {
        if(this.getWorld().getBlockState(pos).isOf(Blocks.TRIPWIRE)) {
            info.cancel();
        }
    }

}
