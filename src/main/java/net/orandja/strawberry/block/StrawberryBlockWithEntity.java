package net.orandja.strawberry.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.orandja.chocoflavor.utils.GlobalUtils;
import net.orandja.strawberry.blockdata.NoteBlockData;
import net.orandja.strawberry.intf.StrawberryBlockState;
import net.orandja.strawberry.intf.StrawberryMarkerEntity;
import net.orandja.strawberry.intf.StrawberryPlayer;

import java.util.function.Consumer;

public abstract class StrawberryBlockWithEntity extends BlockWithEntity implements StrawberryBlockState {

    private final int noteblockID;

    public StrawberryBlockWithEntity(int noteblockID, Consumer<Settings> configurator) {
        super(GlobalUtils.apply(Settings.create(), configurator::accept));
        this.noteblockID = noteblockID;
    }


    @Override
    public float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {
        float f = state.getHardness(world, pos);
        if (f == -1.0f) {
            return 0.0f;
        }
        int i = player.canHarvest(state) ? 30 : 100;
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 2, 50, false, false));
        breakBlockByManager(state, pos, player, world);
        return player.getBlockBreakingSpeed(state) / f / (float)i;
    }

    protected void breakBlockByManager(BlockState state, BlockPos pos, PlayerEntity player, BlockView world) {
        if(player instanceof StrawberryPlayer strawberryPlayer && strawberryPlayer.getBreakerEntity() instanceof StrawberryMarkerEntity markerEntity) {
            if(markerEntity.getMiningPos() != null && markerEntity.getMiningPos() != pos) {
                markerEntity.setTick(-1);
                markerEntity.setBreakingProgress(-1);
                player.getWorld().setBlockBreakingInfo(markerEntity.getEntityID(), markerEntity.getMiningPos(), -1);
                markerEntity.setMiningPos(null);
            }

            float miningSpeedMultiplier = player.getMainHandStack().getMiningSpeedMultiplier(this.getDefaultState());

            if (miningSpeedMultiplier > 1.0F) {
                int efficiency = EnchantmentHelper.getEfficiency(player);
                if (efficiency > 0 && !player.getMainHandStack().isEmpty()) {
                    miningSpeedMultiplier += (float) (efficiency * efficiency + 1);
                }
            }

            if (StatusEffectUtil.hasHaste(player))
                miningSpeedMultiplier *= 1.0F + (StatusEffectUtil.getHasteAmplifier(player) + 1) * 0.2F;

            if (player.isSubmergedIn(FluidTags.WATER) && !EnchantmentHelper.hasAquaAffinity(player))
                miningSpeedMultiplier /= 5F;

            if (!player.isOnGround())
                miningSpeedMultiplier /= 5F;

            miningSpeedMultiplier /= this.getHardness() * (player.canHarvest(state) ? 30f : 100f);

            float breakingProgress = GlobalUtils.run((miningSpeedMultiplier * (markerEntity.getTick() + 1) * 10.0F), progress -> {
                if (progress != markerEntity.getBreakingProgress()) {
                    player.getWorld().setBlockBreakingInfo(markerEntity.getEntityID(), pos, ((int)Math.abs(progress)));
                    return progress;
                }

                return markerEntity.getBreakingProgress();
            });

            markerEntity.addTick(1);

            if (breakingProgress > 10) {
                ((ServerPlayerEntity)player).interactionManager.tryBreakBlock(pos);
                markerEntity.setTick(-1);
                markerEntity.setBreakingProgress(-1);
                markerEntity.setMiningPos(null);
            } else {
                markerEntity.setMiningPos(pos);
            }
        }
    }

    @Override
    public BlockState transform(BlockState blockState) {
        return NoteBlockData.assignStateProperties(noteblockID);
    }
}
