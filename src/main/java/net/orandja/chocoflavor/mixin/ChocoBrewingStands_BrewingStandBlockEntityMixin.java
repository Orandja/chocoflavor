package net.orandja.chocoflavor.mixin;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.orandja.chocoflavor.ChocoBrewingStands;
import net.orandja.chocoflavor.enchantment.EnchantmentDictionary;
import net.orandja.chocoflavor.enchantment.EnchantmentDictionaryUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BrewingStandBlockEntity.class)
public abstract class ChocoBrewingStands_BrewingStandBlockEntityMixin implements ChocoBrewingStands.Handler {

    protected @Unique @Getter EnchantmentDictionary dictionary = createDictionary();
    @Shadow @Getter @Setter int brewTime;
    @Shadow @Getter @Setter int fuel;
    @Shadow @Getter @Setter private DefaultedList<ItemStack> inventory;

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;decrement(I)V", shift = At.Shift.AFTER))
    private static void addFuel(World world, BlockPos pos, BlockState state, BrewingStandBlockEntity blockEntity, CallbackInfo info) {
        EnchantmentDictionaryUtils.compute(blockEntity, ChocoBrewingStands.Handler.class, ChocoBrewingStands.Handler::addFuel);
    }

    @Inject(method = "tick", at = @At(value = "FIELD", shift = At.Shift.BEFORE, ordinal = 1, target = "Lnet/minecraft/block/entity/BrewingStandBlockEntity;brewTime:I"))
    private static void accelerate(World world, BlockPos pos, BlockState state, BrewingStandBlockEntity entity, CallbackInfo info) {
        EnchantmentDictionaryUtils.compute(entity, ChocoBrewingStands.Handler.class, ChocoBrewingStands.Handler::accelerate);
    }

    @Inject(method = "readNbt", at = @At("HEAD"))
    public void readNbt(NbtCompound tag, CallbackInfo info) {
        getDictionary().loadFromNbt(tag);
    }

    @Inject(method = "writeNbt", at = @At("HEAD"))
    public void writeNbt(NbtCompound tag, CallbackInfo info) {
        getDictionary().saveToNbt(tag);
    }
}