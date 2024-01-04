//package net.orandja.chocoflavor.mods.core;
//
//import net.minecraft.block.entity.HopperBlockEntity;
//import net.minecraft.entity.Entity;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.world.World;
//import com.google.common.collect.Lists;
//import net.orandja.chocoflavor.utils.GlobalUtils;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//
//import java.util.List;
//import java.util.function.BiPredicate;
//
//public interface ProtectBlock {
//
//    // returns true if the player can destroy the block
//    List<GlobalUtils.TriPredicate<World, BlockPos, Entity>> ENTITY_CAN_DESTROY = Lists.newArrayList();
//    // returns true if the explosion can destroy the block
//    List<BiPredicate<World, BlockPos>> EXPLOSION_CAN_DESTROY = Lists.newArrayList();
//    // returns false if the hopper can extract item from block **Might be changed in the future
//    List<BiPredicate<World, BlockPos>> HOPPER_CANNOT_EXTRACT = Lists.newArrayList();
//
//    World getWorld();
////    List<BlockPos> getAffectedBlocks();
//
//    default boolean preventsExtract(World world, BlockPos pos) {
//        return HOPPER_CANNOT_EXTRACT.stream().anyMatch(predicate -> predicate.test(world, pos));
//    }
//
//    default boolean preventsExtract(World world, double x, double y, double z) {
//        return preventsExtract(world, new BlockPos((int)Math.floor(x), (int)Math.floor(y), (int)Math.floor(z)));
//    }
//
//    default boolean explosionCanExplode(World world, BlockPos pos) {
//        return EXPLOSION_CAN_DESTROY.stream().noneMatch(predicate -> predicate.test(world, pos));
//    }
//
//    default boolean canDestroy(World world, BlockPos pos, Object entity) {
//        if(entity instanceof Entity entity1) {
//            return ENTITY_CAN_DESTROY.stream().anyMatch(predicate -> predicate.test(world, pos, entity1));
//        }
//
//        return true;
//    }
//
//    default void onPlayerDestroy(World world, BlockPos pos, Object entity, CallbackInfoReturnable<Boolean> info) {
//        if(!canDestroy(world, pos, this)) {
//            info.setReturnValue(true);
//        }
//    }
//
//    static void preventsExtraction(World world, Object maybeHopper, CallbackInfoReturnable<Boolean> info) {
//        if(maybeHopper instanceof HopperBlockEntity hopper && hopper instanceof ProtectBlock hopperProtect && hopperProtect.preventsExtract(world, hopper.getHopperX(), hopper.getHopperY() + 1.0, hopper.getHopperZ())) {
//            info.setReturnValue(false);
//        }
//    }
//
//}
