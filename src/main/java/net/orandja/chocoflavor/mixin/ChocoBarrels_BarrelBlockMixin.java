package net.orandja.chocoflavor.mixin;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.orandja.chocoflavor.ChocoBarrels;
import net.orandja.chocoflavor.ChocoEnchantments;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = {
        BarrelBlock.class
})
public abstract class ChocoBarrels_BarrelBlockMixin extends BlockWithEntity implements ChocoBarrels.Handler {

    protected ChocoBarrels_BarrelBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "onPlaced", at = @At("RETURN"))
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack, CallbackInfo info) {
        onBlockPlaced(world, pos, state, stack);
    }

    @SuppressWarnings("deprecation")
    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
        return enchantLoots(super.getDroppedStacks(state, builder), state, builder);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return this.getCustomTicker();
    }
}
