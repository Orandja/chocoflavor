package net.orandja.chocoflavor.utils;

import com.google.common.collect.Maps;
import net.minecraft.block.BlockState;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.orandja.chocoflavor.ChocoFlavor;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class BlockZone {

    public static final ZoneRepeat NEVER_REPEAT = (_ignored) -> new Pair<>(false, null);
    private final int zMax;
    private final int xMin;
    private final int xMax;
    private final int yMin;
    private final int yMax;
    private final int zMin;

    public BlockZone(int xMin, int xMax, int yMin, int yMax, int zMin, int zMax) {
        this.xMin = Math.min(xMin, xMax);
        this.xMax = Math.max(xMin, xMax);

        this.yMin = Math.min(yMin, yMax);
        this.yMax = Math.max(yMin, yMax);

        this.zMin = Math.min(zMin, zMax);
        this.zMax = Math.max(zMin, zMax);
    }

    public BlockZone(BlockPos pos, int radius) {
        this.xMin = pos.getX() - radius;
        this.xMax = pos.getX() + radius;

        this.yMin = pos.getY() - radius;
        this.yMax = pos.getY() + radius;

        this.zMin = pos.getZ() - radius;
        this.zMax = pos.getZ() + radius;
    }

    public boolean contains(BlockPos pos) {
        return contains(pos.getX(), pos.getY(), pos.getZ());
    }

    public boolean contains(double x, double y, double z) {
        return x <= this.xMax && x >= this.xMin &&
                y <= this.yMax && y >= this.yMin &&
                z <= this.zMax && z >= this.zMin;
    }

    public void iterate(TriConsumer<Integer, Integer, Integer> consumer) {
        for (int y = this.yMin; y <= this.yMax; y++) {
            for (int x = this.xMin; x <= this.xMax; x++) {
                for (int z = this.zMin; z <= this.zMax; z++) {
                    consumer.accept(x, y, z);
                }
            }
        }
    }

    public interface ZoneRepeat {
        Pair<Boolean, BlockPos> repeat(BlockPos origin);
    }

    public List<BlockPos> get(World world, BlockPos origin, int maxIterate, ZoneRepeat zoneRepeat, Predicate<BlockState> validate) {
        return get(world, origin, maxIterate, 0, zoneRepeat, Maps.newHashMap(), validate);
    }

    private List<BlockPos> get(World world, BlockPos origin, int maxIterate, int iteration, ZoneRepeat zoneRepeat, final Map<BlockPos, Boolean> checked, Predicate<BlockState> validate) {
        iterate((x, y, z) -> {
            BlockPos pos = origin.add(x, y, z);
            if (!checked.containsKey(pos)) {
                checked.put(pos, validate.test(world.getBlockState(pos)));
                Pair<Boolean, BlockPos> willRepeat = zoneRepeat.repeat(pos);
                if(willRepeat.getLeft() && iteration < maxIterate) {
                    get(world, willRepeat.getRight(), maxIterate, iteration + 1, zoneRepeat, checked, validate);
                }
            }
        });

        return checked.entrySet().stream().filter(Map.Entry::getValue).map(it -> it.getKey()).collect(Collectors.toList());
    }
}
