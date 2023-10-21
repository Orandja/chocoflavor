package net.orandja.strawberry.mods.farming.block;

import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.orandja.strawberry.mods.core.NoteBlockData;
import net.orandja.strawberry.mods.core.intf.StrawberryBlockState;
import net.orandja.strawberry.mods.resourcepack.StrawberryResourcePackGenerator;

public class SimpleCrop extends CropBlock implements StrawberryBlockState {

    public interface SeedsGetter {
        Item getSeeds();
    }

    private static final VoxelShape[] AGE_TO_SHAPE = new VoxelShape[]{Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 3.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 5.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 6.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 7.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 9.0, 16.0)};

    public final int id;
    public final String model;
    public final SeedsGetter seedsGetter;

    public SimpleCrop(int id, String model, SeedsGetter seedsGetter, AbstractBlock.Settings settings) {
        super(settings);
        this.id = id;
        this.model = model;
        this.seedsGetter = seedsGetter;
    }

    public SimpleCrop(int id, SeedsGetter seedsGetter, AbstractBlock.Settings settings) {
        this(id, null, seedsGetter, settings);
    }

    public SimpleCrop(int id, String model, SeedsGetter seedsGetter) {
        this(id, model, seedsGetter, AbstractBlock.Settings.create().mapColor(MapColor.DARK_GREEN).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.CROP).pistonBehavior(PistonBehavior.DESTROY));
    }

    public SimpleCrop(int id, SeedsGetter seedsGetter) {
        this(id, null, seedsGetter);
    }

    @Override
    protected ItemConvertible getSeedsItem() {
        return this.seedsGetter.getSeeds();
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return AGE_TO_SHAPE[this.getAge(state)];
    }

    @Override
    public BlockState transform(BlockState blockState) {
        return switch (blockState.get(getAgeProperty())) {
            case 2, 3 -> NoteBlockData.assignStateProperties(this.id + 1);
            case 4, 5, 6 -> NoteBlockData.assignStateProperties(this.id + 2);
            case 7 -> NoteBlockData.assignStateProperties(this.id + 3);
            default -> NoteBlockData.assignStateProperties(this.id);
        };
    }

    public String getModel() {
        return this.model != null ? this.model : Registries.BLOCK.getId(this).getPath();
    }

    @Override
    public void register() {
        for(int i = 0; i < 4; i++) {
            register(this.id + i, this.getModel() + i);
        }
    }
}
