//package net.orandja.strawberry.mods.core.mixin;
//
//import com.mojang.authlib.GameProfile;
//import lombok.Getter;
//import lombok.Setter;
//import net.minecraft.entity.EntityType;
//import net.minecraft.entity.MarkerEntity;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.screen.NamedScreenHandlerFactory;
//import net.minecraft.server.network.ServerPlayerEntity;
//import net.minecraft.server.world.ServerWorld;
//import net.minecraft.text.MutableText;
//import net.minecraft.text.Text;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.world.World;
//import net.orandja.chocoflavor.utils.GlobalUtils;
//import net.orandja.strawberry.StrawberryExtraUI;
//import net.orandja.strawberry.intf.StrawberryMarkerEntity;
//import net.orandja.strawberry.intf.StrawberryPlayer;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Shadow;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.Redirect;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//@SuppressWarnings("LombokGetterMayBeUsed")
//@Mixin(ServerPlayerEntity.class)
//public abstract class ServerPlayerEntityMixin extends PlayerEntity implements StrawberryPlayer {
//
//    @Shadow public abstract ServerWorld getServerWorld();
//
//    @Getter @Setter private MarkerEntity breakerEntity;
//
//    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
//        super(world, pos, yaw, gameProfile);
//    }
//
////    @Override
////    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
////        Vec3d vec3d = getVelocity();
////        if (vec3d.y < 0.0) {
////            setVelocity(vec3d.x, -vec3d.y, vec3d.z);
////        }
////        return false;
//////        return super.handleFallDamage(fallDistance, damageMultiplier, damageSource);
////    }
//
//    @Inject(method = "tick", at = @At("TAIL"))
//    public void onTick(CallbackInfo info) {
//        if(breakerEntity != null && breakerEntity.isAlive()) {
//            breakerEntity.setPosition(getPos());
//            return;
//        }
//
//        breakerEntity = new MarkerEntity(EntityType.MARKER, getWorld());
//        //noinspection ConstantValue
//        if(breakerEntity instanceof StrawberryMarkerEntity strawberryMarkerEntity) {
//            strawberryMarkerEntity.setPlayer(ServerPlayerEntity.class.cast(this));
//        }
//        breakerEntity.setPosition(this.getPos());
//        getWorld().spawnEntity(breakerEntity);
//    }
//
////    @Redirect(method = "openHandledScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/NamedScreenHandlerFactory;getDisplayName()Lnet/minecraft/text/Text;"))
////    public Text addExtraGUI(NamedScreenHandlerFactory instance) {
////        if(instance instanceof StrawberryExtraUI.SideUI extraGui && extraGui.isEnabled(instance) && instance.getDisplayName() instanceof MutableText baseName) {
////            return GlobalUtils.apply(instance.getDisplayName().copy(), it -> {
////                extraGui.begin(instance, it, baseName);
////                extraGui.content(instance, it, baseName);
////                extraGui.end(instance, it, baseName);
////            });
////        }
////        return instance.getDisplayName();
////    }
//}
