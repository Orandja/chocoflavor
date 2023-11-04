package net.orandja.strawberry.mods.core.mixin;

import net.minecraft.block.Blocks;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.HeightLimitView;
import net.orandja.chocoflavor.utils.Utils;
import net.orandja.strawberry.mods.core.block.StrawberryBlock;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerChunkManager.class)
public abstract class ServerChunkManagerMixin {

    @Shadow public abstract BlockView getWorld();

    @Inject(method = "markForUpdate", at = @At("HEAD"), cancellable = true)
    public void interceptMarkForUpdate(BlockPos pos, CallbackInfo info) {
        if(this.getWorld().getBlockState(pos).isOf(Blocks.TRIPWIRE)) {
            info.cancel();
        }
    }

}
