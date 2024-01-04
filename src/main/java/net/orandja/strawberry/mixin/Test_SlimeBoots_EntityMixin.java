package net.orandja.strawberry.mixin;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.entity.EntityLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public abstract class Test_SlimeBoots_EntityMixin
        implements Nameable,
        EntityLike,
        CommandOutput {

    @Redirect(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;onEntityLand(Lnet/minecraft/world/BlockView;Lnet/minecraft/entity/Entity;)V"))
    public void onLandOnBlock(Block block, BlockView world, Entity entity) {
        if(ServerPlayerEntity.class.isAssignableFrom(entity.getClass())) {
            Vec3d vec3d = entity.getVelocity();
            if (vec3d.y < 0.0) {
                entity.setVelocity(vec3d.x, -vec3d.y, vec3d.z);
            }
        } else {
            block.onEntityLand(world, entity);
        }
    }

}
