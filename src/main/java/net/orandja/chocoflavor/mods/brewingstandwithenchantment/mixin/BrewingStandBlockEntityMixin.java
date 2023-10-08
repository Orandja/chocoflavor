package net.orandja.chocoflavor.mods.brewingstandwithenchantment.mixin;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.orandja.chocoflavor.mods.brewingstandwithenchantment.BrewingStandWithEnchantment;
import net.orandja.chocoflavor.mods.core.BlockWithEnchantment;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("unused")
@Mixin(BrewingStandBlockEntity.class)
public abstract class BrewingStandBlockEntityMixin implements BrewingStandWithEnchantment {


    @Getter EnchantmentDictionary enchantmentDictionary = new EnchantmentDictionary(VALID_ENCHANTMENTS);
    @Shadow @Getter @Setter int brewTime;
    @Shadow @Getter @Setter int fuel;
    @Shadow @Getter @Setter private DefaultedList<ItemStack> inventory;

    @Redirect(method = "tick", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, ordinal = 0, target = "Lnet/minecraft/block/entity/BrewingStandBlockEntity;fuel:I"))
    private static void setFuelCount(BrewingStandBlockEntity entity, int fuel) {
//        BrewingStandWithEnchantment.addFuelCount(entity, fuel);
        BlockWithEnchantment.compute(entity, BrewingStandWithEnchantment.class, it -> it.setFuel(fuel + it.getEnchantmentDictionary().computeValue(lvl -> lvl * fuel * 0.2D, FUEL.getValue())));
    }

    @Inject(method = "tick", at = @At(value = "FIELD", shift = At.Shift.AFTER, ordinal = 0, target = "Lnet/minecraft/block/entity/BrewingStandBlockEntity;brewTime:I"))
    private static void accelerate(World world, BlockPos pos, BlockState state, BrewingStandBlockEntity entity, CallbackInfo info) {
//        BrewingStandWithEnchantment.accelerateBrew(entity);
        BlockWithEnchantment.compute(entity, BrewingStandWithEnchantment.class, it -> it.setBrewTime(MathHelper.clamp(
                it.getBrewTime() + 1 - Math.max(1, it.getEnchantmentDictionary().computeValue(lvl -> lvl * 2, SPEED.getValue())),
                0, 400
        )));
    }

    @Inject(method = "readNbt", at = @At("HEAD"))
    public void readNbt(NbtCompound tag, CallbackInfo info) {
        getEnchantmentDictionary().loadFromNbt(tag);
    }

    @Inject(method = "writeNbt", at = @At("HEAD"))
    public void writeNbt(NbtCompound tag, CallbackInfo info) {
        getEnchantmentDictionary().saveToNbt(tag);
    }
}
