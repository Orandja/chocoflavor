package net.orandja.strawberry.mods.muffler;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.orandja.chocoflavor.utils.BlockZone;
import net.orandja.chocoflavor.utils.Utils;
import net.orandja.strawberry.mods.core.block.StrawberryBlock;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class MufflerBlock extends StrawberryBlock {

    public MufflerBlock(int noteblockID, String model, Consumer<Settings> configurator) {
        super(noteblockID, model, configurator);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if(world instanceof Muffler muffler) {
            muffler.getMuffledZones().put(pos, new BlockZone(pos, 25));
        }
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return true;
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);

        if(world instanceof Muffler muffler && !muffler.getMuffledZones().containsKey(pos)) {
            muffler.getMuffledZones().put(pos, new BlockZone(pos, 25));
        }
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
        if(world instanceof Muffler muffler) {
            muffler.getMuffledZones().remove(pos);
        }
    }
}
