package net.orandja.strawberry.mods.muffler.mixin;

import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.network.packet.s2c.play.PlaySoundFromEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.orandja.chocoflavor.utils.BlockZone;
import net.orandja.chocoflavor.utils.Utils;
import net.orandja.strawberry.mods.muffler.Muffler;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin implements Muffler {

    @Shadow public abstract void emitGameEvent(GameEvent event, Vec3d emitterPos, GameEvent.Emitter emitter);

    Map<BlockPos, BlockZone> muffledZones = new HashMap<>();

    @Override
    public BlockZone getMuffledZone(BlockPos pos) {
        return muffledZones.get(pos);
    }

    @Override
    public Map<BlockPos, BlockZone> getMuffledZones() {
        return muffledZones;
    }

    @Inject(method = "playSound", at = @At("HEAD"), cancellable = true)
    public void muffleSound(@Nullable PlayerEntity except, double x, double y, double z, RegistryEntry<SoundEvent> sound, SoundCategory category, float volume, float pitch, long seed, CallbackInfo info) {
        if(isMuffled(x, y, z)) {
            info.cancel();
        }
    }

    @Inject(method = "playSoundFromEntity", at = @At("HEAD"), cancellable = true)
    public void muffleSoundFromEntity(@Nullable PlayerEntity except, Entity entity, RegistryEntry<SoundEvent> sound, SoundCategory category, float volume, float pitch, long seed, CallbackInfo info) {
        if(isMuffled(entity.getBlockPos())) {
            info.cancel();
        }
    }

    @Inject(method = "emitGameEvent", at = @At("HEAD"), cancellable = true)
    public void muffleGameEvent(GameEvent event, Vec3d emitterPos, GameEvent.Emitter emitter, CallbackInfo info) {
        if(event.equals(GameEvent.STEP)) {
            if (isMuffled(emitterPos.x, emitterPos.y, emitterPos.z)) {
                Utils.justTry(() -> emitter.sourceEntity().setSilent(true));
                info.cancel();
            } else if (Utils.tryOrDefault(emitter.sourceEntity()::isSilent, false)) {
                emitter.sourceEntity().setSilent(false);
            }
        }
    }

}
