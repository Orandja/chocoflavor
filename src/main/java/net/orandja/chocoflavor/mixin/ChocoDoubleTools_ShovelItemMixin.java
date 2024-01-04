package net.orandja.chocoflavor.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.orandja.chocoflavor.ChocoDoubleTools;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ShovelItem.class)
public class ChocoDoubleTools_ShovelItemMixin extends MiningToolItem implements ChocoDoubleTools.ShovelHandler {

    public ChocoDoubleTools_ShovelItemMixin(float attackDamage, float attackSpeed, ToolMaterial material, TagKey<Block> effectiveBlocks, Settings settings) {
        super(attackDamage, attackSpeed, material, effectiveBlocks, settings);
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        return useDoubleShovels(super.postMine(stack, world, state, pos, miner), world, state, pos, miner);
    }
}
