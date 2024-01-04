//package net.orandja.chocoflavor.mods.blockswithenchantments.mixin;
//
//import lombok.Getter;
//import lombok.Setter;
//import net.minecraft.block.BlockState;
//import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
//import net.minecraft.block.entity.BlockEntityType;
//import net.minecraft.block.entity.LockableContainerBlockEntity;
//import net.minecraft.item.BlockItem;
//import net.minecraft.item.ItemStack;
//import net.minecraft.nbt.NbtCompound;
//import net.minecraft.util.collection.DefaultedList;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.MathHelper;
//import net.minecraft.world.World;
//import net.orandja.chocoflavor.mods.core.BlockWithEnchantment;
//import net.orandja.chocoflavor.mods.blockswithenchantments.FurnaceWithEnchantment;
//import org.objectweb.asm.Opcodes;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Shadow;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.Redirect;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//
//@Mixin(AbstractFurnaceBlockEntity.class)
//public abstract class AbstractFurnaceBlockEntityMixin extends LockableContainerBlockEntity implements FurnaceWithEnchantment {
//    protected AbstractFurnaceBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
//        super(blockEntityType, blockPos, blockState);
//    }
//
//    @Shadow @Getter @Setter int burnTime;
//    @Shadow @Getter @Setter int fuelTime;
//    @Shadow @Getter int cookTime;
//    @Shadow @Getter int cookTimeTotal;
//    @Shadow @Getter protected DefaultedList<ItemStack> inventory;
//    @Getter EnchantmentDictionary enchantmentDictionary = new EnchantmentDictionary(VALID_ENCHANTMENTS);
//
//    @Shadow protected abstract boolean isBurning();
//
//    @Override
//    public boolean vw$burning() { return this.isBurning(); }
//
//    @Override
//    public void vw$setCookTime(int value) {
//        this.cookTime = MathHelper.clamp(value, 0, cookTimeTotal);
//    }
//
//    @Inject(method = "readNbt", at = @At("HEAD"))
//    public void readNbt(NbtCompound tag, CallbackInfo info) {
//        getEnchantmentDictionary().loadFromNbt(tag);
//    }
//
//    @Inject(method = "writeNbt", at = @At("HEAD"))
//    public void writeNbt(NbtCompound tag, CallbackInfo info) {
//        getEnchantmentDictionary().saveToNbt(tag);
//    }
//
//    @Redirect(method = "tick", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, ordinal = 0, target = "Lnet/minecraft/block/entity/AbstractFurnaceBlockEntity;burnTime:I"))
//    private static void decreaseBurnTime(AbstractFurnaceBlockEntity entity, int ignored) {
//        BlockWithEnchantment.compute(entity, FurnaceWithEnchantment.class, it -> {
//            boolean hasUndyingFuel = it.getEnchantmentDictionary().hasAnyEnchantment(UNDYING_FUEL.getValue());
//            boolean slotEmpty = it.getInventory().get(0).isEmpty();
//
//            if(it.vw$burning() && (!hasUndyingFuel || !slotEmpty)) {
//                it.setBurnTime(Math.max(0, it.getBurnTime() - Math.max(1, it.getEnchantmentDictionary().computeValue(lvl -> lvl * SPEED_COEF.getValue(), SPEED.getValue()))));
//            }
//        });
//    }
//
//    @Inject(method = "getFuelTime", at = @At(value = "RETURN"), cancellable = true)
//    public void addBurnTime(ItemStack fuel, CallbackInfoReturnable<Integer> info) {
//        BlockWithEnchantment.computeWithValue(this, FurnaceWithEnchantment.class, FUEL.getValue(), (it, lvl) -> info.setReturnValue((int) (info.getReturnValue() * (1 + (lvl * FUEL_COEF.getValue())))));
//    }
//
//    @Inject(method = "tick", at = @At(value = "FIELD", shift = At.Shift.AFTER, ordinal = 1, target = "Lnet/minecraft/block/entity/AbstractFurnaceBlockEntity;cookTime:I"))
//    private static void accelerateCookTime(World world, BlockPos pos, BlockState state, AbstractFurnaceBlockEntity entity, CallbackInfo info) {
//        BlockWithEnchantment.computeWithValue(entity, FurnaceWithEnchantment.class, SPEED.getValue(), (it, lvl) -> it.vw$setCookTime(it.getCookTime() - 1 + Math.max(1, lvl * 2)));
//    }
//
//    @Inject(method = "tick", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/block/entity/AbstractFurnaceBlockEntity;setLastRecipe(Lnet/minecraft/recipe/RecipeEntry;)V"))
//    private static void increaseOutputAmount(World world, BlockPos pos, BlockState state, AbstractFurnaceBlockEntity entity, CallbackInfo info) {
//        BlockWithEnchantment.compute(entity, FurnaceWithEnchantment.class, FORTUNE.getValue(), it -> {
//            if(!(it.getInventory().get(2).getItem() instanceof BlockItem)) {
//                it.getInventory().get(2).increment(Math.max(0, it.getEnchantmentDictionary().computeValue(lvl -> world.getRandom().nextInt(lvl + 2), FORTUNE.getValue()) - 1));
//            }
//        });
//    }
//}