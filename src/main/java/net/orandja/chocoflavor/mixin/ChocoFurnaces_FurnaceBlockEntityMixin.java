package net.orandja.chocoflavor.mixin;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.orandja.chocoflavor.ChocoFurnaces;
import net.orandja.chocoflavor.enchantment.EnchantmentDictionary;
import net.orandja.chocoflavor.utils.GlobalUtils;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class ChocoFurnaces_FurnaceBlockEntityMixin extends LockableContainerBlockEntity implements ChocoFurnaces.Handler {
    protected ChocoFurnaces_FurnaceBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Shadow @Getter @Setter int burnTime;
    @Shadow @Getter @Setter int fuelTime;
    @Shadow @Getter int cookTime;
    @Shadow @Getter int cookTimeTotal;
    @Shadow @Getter protected DefaultedList<ItemStack> inventory;
    @Unique @Getter EnchantmentDictionary dictionary = createDictionary();

    @Shadow protected abstract boolean isBurning();
    @Override
    public boolean furnaceBurning() { return this.isBurning(); }

    @Override
    public void setCookTime(int value) {
        this.cookTime = MathHelper.clamp(value, 0, cookTimeTotal);
    }

    @Inject(method = "readNbt", at = @At("HEAD"))
    public void readNbt(NbtCompound tag, CallbackInfo info) {
        getDictionary().loadFromNbt(tag);
    }

    @Inject(method = "writeNbt", at = @At("HEAD"))
    public void writeNbt(NbtCompound tag, CallbackInfo info) {
        getDictionary().saveToNbt(tag);
    }

    @Redirect(method = "tick", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, ordinal = 0, target = "Lnet/minecraft/block/entity/AbstractFurnaceBlockEntity;burnTime:I"))
    private static void decreaseBurnTime(AbstractFurnaceBlockEntity entity, int ignored) {
        GlobalUtils.applyAs(entity, ChocoFurnaces.Handler.class, ChocoFurnaces.Handler::decreaseBurnTime);
    }

    @Inject(method = "getFuelTime", at = @At(value = "RETURN"), cancellable = true)
    public void addBurnTime(ItemStack fuel, CallbackInfoReturnable<Integer> info) {
        this.addMoreBurnTime(info);
    }

    @Inject(method = "tick", at = @At(value = "FIELD", shift = At.Shift.AFTER, ordinal = 1, target = "Lnet/minecraft/block/entity/AbstractFurnaceBlockEntity;cookTime:I"))
    private static void accelerateCookTime(World world, BlockPos pos, BlockState state, AbstractFurnaceBlockEntity entity, CallbackInfo info) {
        GlobalUtils.applyAs(entity, ChocoFurnaces.Handler.class, ChocoFurnaces.Handler::accelerate);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/block/entity/AbstractFurnaceBlockEntity;setLastRecipe(Lnet/minecraft/recipe/RecipeEntry;)V"))
    private static void increaseOutputAmount(World world, BlockPos pos, BlockState state, AbstractFurnaceBlockEntity entity, CallbackInfo info) {
        GlobalUtils.applyAs(entity, ChocoFurnaces.Handler.class, ChocoFurnaces.Handler::increaseOutput);
    }

    @Override
    public World getChocoWorld() {
        return getWorld();
    }
}
