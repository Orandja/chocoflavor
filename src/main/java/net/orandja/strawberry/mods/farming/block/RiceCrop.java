//package net.orandja.strawberry.mods.farming.crops;
//
//import net.minecraft.block.*;
//import net.minecraft.block.piston.PistonBehavior;
//import net.minecraft.item.*;
//import net.minecraft.sound.BlockSoundGroup;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.shape.VoxelShape;
//import net.minecraft.world.BlockView;
//import net.orandja.strawberry.mods.core.NoteBlockData;
//import net.orandja.strawberry.mods.core.intf.BlockStateTransformer;
//import net.orandja.strawberry.mods.farming.Farming;
//
//public class RiceCrop extends CropBlock implements BlockStateTransformer {
//
//    public static final int ID = 1;
//
//    private static final VoxelShape[] AGE_TO_SHAPE = new VoxelShape[]{Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 3.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 5.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 6.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 7.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 9.0, 16.0)};
//
//    public RiceCrop() {
//        super(AbstractBlock.Settings.create().mapColor(MapColor.DARK_GREEN).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.CROP).pistonBehavior(PistonBehavior.DESTROY));
//    }
//
//    @Override
//    protected ItemConvertible getSeedsItem() {
//        return Farming.RICE;
//    }
//
//    @Override
//    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
//        return AGE_TO_SHAPE[this.getAge(state)];
//    }
//
//    @Override
//    public BlockState transform(BlockState blockState) {
//        return switch (blockState.get(getAgeProperty())) {
//            case 2, 3 -> NoteBlockData.assignStateProperties(ID + 1);
//            case 4, 5, 6 -> NoteBlockData.assignStateProperties(ID + 2);
//            case 7 -> NoteBlockData.assignStateProperties(ID + 3);
//            default -> NoteBlockData.assignStateProperties(ID);
//        };
//    }
//}
