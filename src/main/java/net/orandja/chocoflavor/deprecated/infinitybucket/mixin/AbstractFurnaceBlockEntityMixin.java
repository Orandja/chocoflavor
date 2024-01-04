//package net.orandja.chocoflavor.mods.infinitybucket.mixin;
//
//import net.minecraft.block.BlockState;
//import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
//import net.minecraft.block.entity.BlockEntityType;
//import net.minecraft.block.entity.LockableContainerBlockEntity;
//import net.minecraft.item.Item;
//import net.minecraft.item.ItemStack;
//import net.minecraft.util.math.BlockPos;
//import net.orandja.chocoflavor.mods.infinitybucket.InfinityBucket;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Redirect;
//
//@Mixin(AbstractFurnaceBlockEntity.class)
//public abstract class AbstractFurnaceBlockEntityMixin extends LockableContainerBlockEntity implements InfinityBucket {
//    protected AbstractFurnaceBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
//        super(blockEntityType, blockPos, blockState);
//    }
//
//    @Redirect(method = "craftRecipe", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"))
//    private static boolean craftRecipe(ItemStack stack, Item item) {
//        return stack.isOf(item) && !InfinityBucket.isInfinity(stack);
//    }
//}