package net.orandja.strawberry.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.entity.DropperBlockEntity;
import net.minecraft.block.enums.Instrument;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.orandja.chocoflavor.utils.GlobalUtils;
import net.orandja.strawberry.blockentity.IngotChargerBlockEntity;
import net.orandja.strawberry.intf.StrawberryLightningRodInteractable;

public class IngotChargerBlock extends StrawberryBlockWithEntity implements StrawberryLightningRodInteractable {

    public IngotChargerBlock(int noteblockID) {
        super(noteblockID, it -> it.mapColor(MapColor.STONE_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(3.5f));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }

        GlobalUtils.applyAs(world.getBlockEntity(pos), IngotChargerBlockEntity.class, player::openHandledScreen);
        return ActionResult.SUCCESS;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new IngotChargerBlockEntity(pos, state);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (itemStack.hasCustomName()) {
            GlobalUtils.applyAs(world.getBlockEntity(pos), IngotChargerBlockEntity.class, it -> it.setCustomName(itemStack.getName()));
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.isOf(newState.getBlock())) {
            return;
        }
        GlobalUtils.applyAs(world.getBlockEntity(pos), IngotChargerBlockEntity.class, it -> {
            ItemScatterer.spawn(world, pos, it);
            world.updateComparators(pos, this);
        });

        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }

    @Override
    public void onLightningInteract(LightningEntity lightning, BlockPos pos) {
        GlobalUtils.applyAs(lightning.getWorld().getBlockEntity(pos), IngotChargerBlockEntity.class, it -> {
            it.chargeIngots(lightning.getChanneler() == null ? 2 : 1);
        });
    }
}
