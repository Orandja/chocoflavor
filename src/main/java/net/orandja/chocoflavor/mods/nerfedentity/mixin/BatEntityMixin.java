package net.orandja.chocoflavor.mods.nerfedentity.mixin;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.world.World;
import net.orandja.chocoflavor.mods.nerfedentity.NerfedEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BatEntity.class)
public abstract class BatEntityMixin extends AmbientEntity implements NerfedEntity {
    protected BatEntityMixin(EntityType<? extends AmbientEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique @Getter @Setter public short tickCooldown = 30;

    @Inject(at = @At("HEAD"), method = "mobTick", cancellable = true)
    protected void mobTick(CallbackInfo info) {
        nerfAI(info);
    }

    @Override
    public boolean vw$isMobDead() {
        return this.isDead();
    }
}