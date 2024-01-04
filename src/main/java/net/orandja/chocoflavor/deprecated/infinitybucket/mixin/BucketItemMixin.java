//package net.orandja.chocoflavor.mods.infinitybucket.mixin;
//
//import net.minecraft.block.Block;
//import net.minecraft.block.BlockState;
//import net.minecraft.block.BlockWithEntity;
//import net.minecraft.block.FluidDrainable;
//import net.minecraft.enchantment.EnchantmentHelper;
//import net.minecraft.enchantment.Enchantments;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.fluid.Fluid;
//import net.minecraft.fluid.Fluids;
//import net.minecraft.item.BucketItem;
//import net.minecraft.item.FluidModificationItem;
//import net.minecraft.item.Item;
//import net.minecraft.item.ItemStack;
//import net.minecraft.util.Hand;
//import net.minecraft.util.TypedActionResult;
//import net.minecraft.util.hit.BlockHitResult;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.Direction;
//import net.minecraft.world.RaycastContext;
//import net.minecraft.world.World;
//import net.orandja.chocoflavor.mods.core.BlockWithEnchantment;
//import net.orandja.chocoflavor.mods.infinitybucket.InfinityBucket;
//import org.spongepowered.asm.mixin.Final;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Shadow;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.Redirect;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//
//@Mixin(BucketItem.class)
//public abstract class BucketItemMixin extends Item implements FluidModificationItem, InfinityBucket {
//    @Shadow @Final private Fluid fluid;
//
//    public BucketItemMixin(Settings settings) {
//        super(settings);
//    }
//
//    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getBlock()Lnet/minecraft/block/Block;", ordinal = 0))
//    public void onEmptyingWithBucket(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
//        ItemStack itemStack = user.getStackInHand(hand);
//        if(itemStack.hasNbt() && itemStack.getNbt().contains(ItemStack.ENCHANTMENTS_KEY)) {
//            if(ENABLING.anyMatch(it -> EnchantmentHelper.getLevel(it, itemStack) > 0)) {
//                CAPACITY.computeWithValue(itemStack, level -> {
//                    BlockHitResult blockHitResult = raycast(world, user, RaycastContext.FluidHandling.SOURCE_ONLY);
//                    BlockPos blockPos = blockHitResult.getBlockPos();
//                    Direction direction = blockHitResult.getSide();
//                    BlockPos blockPos2 = blockPos.offset(direction);
//                    for(int x = -level; x <= level; x++) {
//                        for(int y = -level; y <= level; y++) {
//                            for(int z = -level; z <= level; z++) {
//                                if(!(x == 0 && y == 0 && z == 0)) {
//                                    BlockPos f = blockPos2.add(x, y, z);
//                                    if (world.canPlayerModifyAt(user, f) && user.canPlaceOn(f, direction, itemStack)) {
//                                        BlockState blockState = world.getBlockState(f);
//                                        if (blockState.getBlock() instanceof FluidDrainable fluidDrainable) {
//                                            fluidDrainable.tryDrainFluid(user, world, f, blockState);
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                });
//            }
//        }
//    }
//
//    @Redirect(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemUsage;exchangeStack(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;"))
//    public ItemStack onEmptyBucket(ItemStack inputStack, PlayerEntity player, ItemStack outputStack) {
//        return InfinityBucket.handleInfinityBucket(inputStack, player, outputStack);
//    }
//
//    @Redirect(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/BucketItem;getEmptiedStack(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/item/ItemStack;"))
//    public ItemStack onWaterBucket(ItemStack stack, PlayerEntity player) {
//        return InfinityBucket.handleInfinityBucket(stack, player);
//    }
//
//}
