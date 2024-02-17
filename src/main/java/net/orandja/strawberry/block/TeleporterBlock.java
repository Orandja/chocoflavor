package net.orandja.strawberry.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.Instrument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.orandja.chocoflavor.utils.GlobalUtils;
import net.orandja.strawberry.StrawberryCustomBlocks;
import net.orandja.strawberry.blockentity.TeleporterBlockEntity;

public class TeleporterBlock extends StrawberryBlockWithEntity {

    public TeleporterBlock(int noteblockID) {
        super(noteblockID, it -> it.mapColor(MapColor.STONE_GRAY).instrument(Instrument.BASEDRUM).strength(3.5f));
    }

    @Override
    public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        if (!world.isClient) {
            GlobalUtils.applyAs(world.getBlockEntity(pos), TeleporterBlockEntity.class, it -> {
                if (player.isSneaking()) it.tryTeleport(player);
                else it.changeIndex(player);
            });
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }

        ItemStack stack = player.getStackInHand(hand);
        if(stack.isOf(StrawberryCustomBlocks.TELEPORTING_ESSENCE)) {
            if(GlobalUtils.runAsWithDefault(world.getBlockEntity(pos), TeleporterBlockEntity.class, false, it -> it.addLocation(player, stack))) {
                stack.decrement(1);
                return ActionResult.CONSUME;
            }

            return ActionResult.FAIL;
        }

        GlobalUtils.applyAs(world.getBlockEntity(pos), TeleporterBlockEntity.class, it -> {
            if (player.isSneaking()) it.tryTeleport(player);
            else it.announceIndex(player);
        });

        return ActionResult.SUCCESS;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TeleporterBlockEntity(pos, state);
    }
}
