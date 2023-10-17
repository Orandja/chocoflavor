package net.orandja.strawberry.mods.farming.block;

import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;
import net.minecraft.world.event.listener.GameEventListener;
import net.orandja.strawberry.mods.core.NoteBlockData;
import net.orandja.strawberry.mods.core.intf.BlockStateTransformer;
import net.orandja.strawberry.mods.farming.block.entity.BoilerBlockEntity;
import org.jetbrains.annotations.Nullable;

public class BoilerBlock extends BlockWithEntity implements BlockStateTransformer {

    public BoilerBlock() {
        super(AbstractBlock.Settings.create());
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BoilerBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof BoilerBlockEntity boiler) {
            player.openHandledScreen(boiler);
        }
        return ActionResult.CONSUME;
    }

//    @Override
//    @Nullable
//    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
//        return world.isClient ? null : BrewingStandBlock.validateTicker(type, BlockEntityType.BREWING_STAND, BrewingStandBlockEntity::tick);
//    }

    @Override
    public BlockState transform(BlockState blockState) {
        return NoteBlockData.assignStateProperties(17);
    }
}
