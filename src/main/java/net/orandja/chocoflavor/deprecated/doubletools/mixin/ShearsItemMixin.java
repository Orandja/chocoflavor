//package net.orandja.chocoflavor.mods.doubletools.mixin;
//
//import net.minecraft.block.BlockState;
//import net.minecraft.entity.LivingEntity;
//import net.minecraft.item.ItemStack;
//import net.minecraft.item.ShearsItem;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.world.World;
//import net.orandja.chocoflavor.mods.doubletools.DoubleTools;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//
//@Mixin(ShearsItem.class)
//public abstract class ShearsItemMixin implements DoubleTools {
//
//    @Inject(method = "postMine", at = @At("RETURN"))
//    public void useDoubleTools(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner, CallbackInfoReturnable<Boolean> info) {
//        useShears(info.getReturnValue(), stack, world, state, pos, miner, this.getClass());
//    }
//
//}
