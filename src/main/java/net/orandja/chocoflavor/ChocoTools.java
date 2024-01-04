package net.orandja.chocoflavor;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CocoaBlock;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class ChocoTools {
    private ChocoTools() {}

    public static void init() {
        ChocoEnchantments.createRegistry(Items.STICK)
                .allowInAnvil(Enchantments.KNOCKBACK);
        ChocoEnchantments.createRegistry(Items.SHEARS)
                .allowInAnvil(Enchantments.FORTUNE);
    }

    public interface HoeHandler {
        default void useOnCocoaBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> info) {
            World world = context.getWorld();
            if(!world.isClient) {
                BlockPos pos = context.getBlockPos();
                PlayerEntity player = context.getPlayer();
                BlockState state = world.getBlockState(pos);
                if(state.getBlock() instanceof CocoaBlock) {
                    if(state.get(CocoaBlock.AGE) >= CocoaBlock.MAX_AGE) {
                        world.setBlockState(pos, state.with(CocoaBlock.AGE, 0), 2);
                        Block.dropStacks(state, world, pos, null, player, player.getStackInHand(context.getHand()));
                        info.setReturnValue(ActionResult.SUCCESS);
                    }
                }
            }
        }
    }

    public interface MaterialSupplier {
        ToolMaterial getMaterial();
    }
}
