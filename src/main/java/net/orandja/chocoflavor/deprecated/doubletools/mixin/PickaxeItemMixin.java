//package net.orandja.chocoflavor.mods.doubletools.mixin;
//
//import net.minecraft.block.Block;
//import net.minecraft.block.BlockState;
//import net.minecraft.entity.LivingEntity;
//import net.minecraft.item.ItemStack;
//import net.minecraft.item.MiningToolItem;
//import net.minecraft.item.PickaxeItem;
//import net.minecraft.item.ToolMaterial;
//import net.minecraft.registry.tag.TagKey;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.world.World;
//import net.orandja.chocoflavor.mods.doubletools.DoubleTools;
//import org.spongepowered.asm.mixin.Mixin;
//
//@Mixin(PickaxeItem.class)
//public abstract class PickaxeItemMixin extends MiningToolItem implements DoubleTools {
//
//    public PickaxeItemMixin(float f, float g, ToolMaterial toolMaterial, TagKey<Block> tagKey, Settings settings) {
//        super(f, g, toolMaterial, tagKey, settings);
//    }
//
//    @Override
//    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
//        return usePickaxe(super.postMine(stack, world, state, pos, miner), stack, world, state, pos, miner, PickaxeItem.class);
//    }
//}
