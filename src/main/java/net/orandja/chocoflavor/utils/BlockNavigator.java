package net.orandja.chocoflavor.utils;

import net.minecraft.block.BlockState;
import net.minecraft.data.client.BlockStateVariantMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BlockNavigator {

    public enum State {
        PASS,
        CANCEL,
        ACCEPT,
        SAVE,
        ACCEPT_AND_SAVE;
    }

    private final World world;
    private BlockPos position;
    private List<BlockPos> navigated = new ArrayList<>();
    private List<BlockPos> saved = new ArrayList<>();
    private Consumer<BlockPos> onSavedConsumer;

    public BlockNavigator(World world, BlockPos position) {
        this.world = world;
        this.position = position;
    }

    public BlockNavigator navigate(BiFunction<BlockPos, BlockState, Direction> nextDirection) {
        Direction direction = this.navigated.contains(this.position) ?
                nextDirection.apply(this.position, this.world.getBlockState(this.position)) :
                nextDirection.apply(null, null);
        navigated.add(this.position);
        if(direction != null) {
            this.position = this.position.offset(direction, 1);
            navigate(nextDirection);
        }
        return this;
    }

    public BlockNavigator navigate(TriFunction<BlockNavigator, BlockPos, BlockState, Direction> nextDirection) {
        return this.navigate((blockPos, blockState) -> nextDirection.apply(this, blockPos, blockState));
    }

    public BlockNavigator navigateTo(BiFunction<BlockPos, BlockState, BlockPos> nextPos) {
        BlockPos pos = this.navigated.contains(this.position) ?
                nextPos.apply(this.position, this.world.getBlockState(this.position)) :
                nextPos.apply(null, null);
        navigated.add(this.position);
        if(pos != null) {
            this.position = pos;
            navigateTo(nextPos);
        }
        return this;
    }

    public BlockNavigator navigateTo(TriFunction<BlockNavigator, BlockPos, BlockState, BlockPos> nextDirection) {
        return this.navigateTo((blockPos, blockState) -> nextDirection.apply(this, blockPos, blockState));
    }

    public BlockNavigator scan(int xMin, int xMax, int yMin, int yMax, int zMin, int zMax, BiConsumer<BlockPos, BlockState> consumer) {
        return this.scanAt(this.position, xMin, xMax, yMin, yMax, zMin, zMax, consumer);
    }

    public BlockNavigator scan(int[] zone, BiConsumer<BlockPos, BlockState> consumer) {
        return this.scan(zone[0], zone[1], zone[2], zone[3], zone[4], zone[5], consumer);
    }

    public BlockNavigator scan(int xMin, int xMax, int yMin, int yMax, int zMin, int zMax, GlobalUtils.TriConsumer<BlockNavigator, BlockPos, BlockState> consumer) {
        return this.scan(xMin, xMax, yMin, yMax, zMin, zMax, (pos, state) -> consumer.accept(this, pos, state));
    }

    public BlockNavigator scan(int[] zone, GlobalUtils.TriConsumer<BlockNavigator, BlockPos, BlockState> consumer) {
        return this.scan(zone[0], zone[1], zone[2], zone[3], zone[4], zone[5], (pos, state) -> consumer.accept(this, pos, state));
    }


    public BlockNavigator scanAt(BlockPos position, int xMin, int xMax, int yMin, int yMax, int zMin, int zMax, BiConsumer<BlockPos, BlockState> consumer) {
        for (int y = yMin; y <= yMax; y++) {
            for (int x = xMin; x <= xMax; x++) {
                for (int z = zMin; z <= zMax; z++) {
                    BlockPos pos = position.add(x, y, z);
                    if(!this.navigated.contains(pos)) {
                        this.navigated.add(pos);
                        consumer.accept(pos, this.world.getBlockState(pos));
                    }
                }
            }
        }
        return this;
    }
    public BlockNavigator scanAt(BlockPos position, int[] zone, BiConsumer<BlockPos, BlockState> consumer) {
        return this.scanAt(position, zone[0], zone[1], zone[2], zone[3], zone[4], zone[5], consumer);
    }
    public BlockNavigator scanAt(BlockPos position, int xMin, int xMax, int yMin, int yMax, int zMin, int zMax, GlobalUtils.TriConsumer<BlockNavigator, BlockPos, BlockState> consumer) {
        return this.scanAt(position, xMin, xMax, yMin, yMax, zMin, zMax, (pos, state) -> consumer.accept(this, pos, state));
    }
    public BlockNavigator scanAt(BlockPos position, int[] zone, GlobalUtils.TriConsumer<BlockNavigator, BlockPos, BlockState> consumer) {
        return this.scanAt(position, zone[0], zone[1], zone[2], zone[3], zone[4], zone[5], (pos, state) -> consumer.accept(this, pos, state));
    }



    public BlockNavigator scanWithItself(int[] zone, Consumers.Tri<BlockPos, BlockState, Consumer<BlockPos>> consumer) {
        return this.scan(zone[0], zone[1], zone[2], zone[3], zone[4], zone[5], (pos, state) -> consumer.accept(pos, state, oPos -> this.scanWithItself(zone, consumer)));
    }

    public BlockNavigator scanWithItself(int xMin, int xMax, int yMin, int yMax, int zMin, int zMax, Consumers.Quad<BlockNavigator, BlockPos, BlockState, Consumer<BlockPos>> consumer) {
        return this.scan(xMin, xMax, yMin, yMax, zMin, zMax, (pos, state) -> consumer.accept(this, pos, state, oPos -> this.scanWithItself(xMin, xMax, yMin, yMax, zMin, zMax, consumer)));
    }

    public BlockNavigator scanWithItself(int[] zone, Consumers.Quad<BlockNavigator, BlockPos, BlockState, Consumer<BlockPos>> consumer) {
        return this.scan(zone[0], zone[1], zone[2], zone[3], zone[4], zone[5], (pos, state) -> consumer.accept(this, pos, state, oPos -> this.scanWithItself(zone, consumer)));
    }

    public BlockNavigator scanAtWithItself(BlockPos position, int[] zone, Consumers.Tri<BlockPos, BlockState, Consumer<BlockPos>> consumer) {
        return this.scanAt(position, zone[0], zone[1], zone[2], zone[3], zone[4], zone[5], (pos, state) -> consumer.accept(pos, state, oPos -> this.scanAtWithItself(position, zone, consumer)));
    }
    public BlockNavigator scanAtWithItself(BlockPos position, int xMin, int xMax, int yMin, int yMax, int zMin, int zMax, Consumers.Quad<BlockNavigator, BlockPos, BlockState, Consumer<BlockPos>> consumer) {
        return this.scanAt(position, xMin, xMax, yMin, yMax, zMin, zMax, (pos, state) -> consumer.accept(this, pos, state, oPos -> this.scanAtWithItself(oPos, xMin, xMax, yMin, yMax, zMin, zMax, consumer)));
    }
    public BlockNavigator scanAtWithItself(BlockPos position, int[] zone, Consumers.Quad<BlockNavigator, BlockPos, BlockState, Consumer<BlockPos>> consumer) {
        return this.scanAt(position, zone[0], zone[1], zone[2], zone[3], zone[4], zone[5], (pos, state) -> consumer.accept(this, pos, state, oPos -> this.scanAtWithItself(oPos, zone, consumer)));
    }

    public BlockNavigator save(BlockPos pos) {
        if(this.onSavedConsumer != null) this.onSavedConsumer.accept(pos);
        else this.saved.add(pos);
        return this;
    }

    public BlockNavigator getSaved(Consumer<BlockPos> consumer) {
        this.saved.forEach(consumer);
        return this;
    }

    public BlockNavigator onSaved(Consumer<BlockPos> consumer) {
        this.onSavedConsumer = consumer;
        return this;
    }

    public int visitedCount() {
        return this.navigated.size();
    }

}
