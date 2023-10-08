package net.orandja.chocoflavor.mods.deepstoragebarrel.mixin;

import net.minecraft.block.BarrelBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.orandja.chocoflavor.mods.deepstoragebarrel.DeepStorageBarrel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@SuppressWarnings("unused")
@Mixin(BarrelBlock.class)
public abstract class BarrelBlockMixin extends BlockWithEntity implements DeepStorageBarrel {

    protected BarrelBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "onPlaced", at = @At("RETURN"))
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack, CallbackInfo info) {
        onBlockPlaced(world, pos, state, placer, stack);
    }

    @SuppressWarnings("deprecation")
    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
        return enchantLoots(super.getDroppedStacks(state, builder), state, builder);
    }

    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, BlockEntityType.BARREL, DeepStorageBarrel::tick);
    }
}
