package net.orandja.chocoflavor.mixin;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import lombok.Getter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.orandja.chocoflavor.ChocoWorlds;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Explosion.class)
public abstract class ChocoWorlds_ExplosionMixin implements ChocoWorlds.Handler {

    @Final @Shadow @Getter private World world;

    @Shadow @Final private ObjectArrayList<BlockPos> affectedBlocks;

    @Inject(method = "affectWorld", at = @At("HEAD"))
    void affectWorld(boolean particles, CallbackInfo info) {
        List<BlockPos> newBlocks = this.affectedBlocks.stream().filter(block -> getWorld() != null && explosionCanExplode(getWorld(), block)).toList();
        affectedBlocks.clear();
        affectedBlocks.addAll(newBlocks);
    }
}