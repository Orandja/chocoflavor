package net.orandja.strawberry.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.Instrument;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.orandja.chocoflavor.utils.BlockUtils;
import net.orandja.strawberry.block.StrawberryBlock;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class MufflerBlock extends StrawberryBlock {

    public MufflerBlock(int noteblockID) {
        super(noteblockID, it -> it.mapColor(MapColor.STONE_GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(3.5f));
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if(world instanceof Handler muffler) {
            muffler.getMuffledZones().put(pos, new BlockUtils.Zone(pos, 25));
        }
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return true;
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);

        if(world instanceof Handler muffler && !muffler.getMuffledZones().containsKey(pos)) {
            muffler.getMuffledZones().put(pos, new BlockUtils.Zone(pos, 25));
        }
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
        if(world instanceof Handler muffler) {
            muffler.getMuffledZones().remove(pos);
        }
    }

    public interface Handler {

        BlockUtils.Zone getMuffledZone(BlockPos pos);
        Map<BlockPos, BlockUtils.Zone> getMuffledZones();

        default boolean isMuffled(BlockPos pos) {
            return isMuffled(pos.getX(), pos.getY(), pos.getZ());
        }

        default boolean isMuffled(double x, double y, double z) {
            for (BlockUtils.Zone zone : getMuffledZones().values()) {
                if(zone.contains(x, y ,z)) {
                    return true;
                }
            }

            return false;
        }
    }
}
