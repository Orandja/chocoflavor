package net.orandja.strawberry;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.block.enums.Instrument;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.orandja.chocoflavor.ChocoRecipes;
import net.orandja.chocoflavor.recipe.CloudShulkerBoxRecipe;
import net.orandja.chocoflavor.utils.GlobalUtils;
import net.orandja.strawberry.block.IngotChargerBlock;
import net.orandja.strawberry.block.StrawberryBlock;
import net.orandja.strawberry.block.TeleporterBlock;
import net.orandja.strawberry.blockentity.IngotChargerBlockEntity;
import net.orandja.strawberry.blockentity.TeleporterBlockEntity;
import net.orandja.strawberry.intf.StrawberryBlockEntity;
import net.orandja.strawberry.intf.StrawberryBlockState;
import net.orandja.strawberry.item.ChargedIngotItem;
import net.orandja.strawberry.item.StrawberryBlockItem;
import net.orandja.strawberry.block.MufflerBlock;
import net.orandja.strawberry.blockdata.TripWireBlockData;
import net.orandja.strawberry.item.StrawberryItem;
import net.orandja.strawberry.item.TeleportingEssence;
import net.orandja.strawberry.recipe.ChargedIngotShapedRecipe;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

public class StrawberryCustomBlocks {

    private static final BlockState fullTripWireState = Blocks.TRIPWIRE.getDefaultState().with(TripwireBlock.POWERED, true).with(TripwireBlock.ATTACHED, true).with(TripwireBlock.DISARMED, true).with(TripwireBlock.NORTH, true).with(TripwireBlock.SOUTH, true).with(TripwireBlock.EAST, true).with(TripwireBlock.WEST, true);
    private static final BlockState wheatStage0State = TripWireBlockData.fromID(1).generateState();

    public static Block REINFORCED_OBSIDIAN;
    public static Item REINFORCED_OBSIDIAN_ITEM;

    public static Block MUFFLER_BLOCK;
    public static Item MUFFLER_ITEM;

    public static Item DIAMOND_INGOT;
    public static Item CHARGED_DIAMOND_INGOT;
    public static Item TELEPORTING_ESSENCE;

    public static Block TELEPORTER_BLOCK;
    public static Item TELEPORTER_BLOCK_ITEM;
    public static BlockEntityType<TeleporterBlockEntity> TELEPORTER_BLOCK_ENTITY_TYPE;


    public static Block INGOT_CHARGER_BLOCK;
    public static Item INGOT_CHARGER_BLOCK_ITEM;
    public static BlockEntityType<IngotChargerBlockEntity> INGOT_CHARGER_ENTITY_TYPE;

    public static void init() {
        REINFORCED_OBSIDIAN = Blocks.register("reinforced_obsidian", new StrawberryBlock(24, it -> {
            it.mapColor(MapColor.BLACK).instrument(Instrument.BASEDRUM).requiresTool().strength(150.0f, 1200.0f);
        }));
        REINFORCED_OBSIDIAN_ITEM = Items.register(new StrawberryBlockItem(REINFORCED_OBSIDIAN, 24, new Item.Settings()));

        MUFFLER_BLOCK = Blocks.register("muffler", new MufflerBlock(25));
        MUFFLER_ITEM = Items.register(new StrawberryBlockItem(MUFFLER_BLOCK, 25, new Item.Settings()));

        INGOT_CHARGER_BLOCK = Blocks.register("ingot_charger", new IngotChargerBlock(27));
        INGOT_CHARGER_BLOCK_ITEM = Items.register(new StrawberryBlockItem(INGOT_CHARGER_BLOCK, 27, new Item.Settings()));
        INGOT_CHARGER_ENTITY_TYPE = StrawberryBlockEntity.create("ingot_charger", BlockEntityType.Builder.create(IngotChargerBlockEntity::new, INGOT_CHARGER_BLOCK));

        DIAMOND_INGOT = Items.register("diamond_ingot", new StrawberryItem(Items.DIAMOND, 1, new Item.Settings()));
        CHARGED_DIAMOND_INGOT = Items.register("charged_diamond_ingot", new ChargedIngotItem(Items.DIAMOND, 2, new Item.Settings()));

        TELEPORTING_ESSENCE = Items.register("teleporting_essence", new TeleportingEssence(Items.MAP, 2, new Item.Settings()));

        TELEPORTER_BLOCK = Blocks.register("teleporter", new TeleporterBlock(26));
        TELEPORTER_BLOCK_ITEM = Items.register(new StrawberryBlockItem(TELEPORTER_BLOCK, 26, new Item.Settings()));
        TELEPORTER_BLOCK_ENTITY_TYPE = StrawberryBlockEntity.create("teleporter", BlockEntityType.Builder.create(TeleporterBlockEntity::new, TELEPORTER_BLOCK));
        ChocoRecipes.addShapedRecipe(new Identifier("chocoflavor", "teleporter"), ChargedIngotShapedRecipe::new);
    }

    public interface BlockUpdateHandler {
        default void onBlockUpdate(BlockState state, PacketByteBuf buf, CallbackInfo info) {
            if(state.getBlock() instanceof StrawberryBlockState blockStateTransformer) {
                buf.writeRegistryValue(Block.STATE_IDS, blockStateTransformer.transform(state));
                info.cancel();
                return;
            }

            if(state.isOf(Blocks.NOTE_BLOCK)) {
                buf.writeRegistryValue(Block.STATE_IDS, Blocks.NOTE_BLOCK.getDefaultState());
                info.cancel();
            }

            if(state.isOf(Blocks.TRIPWIRE)) {
                buf.writeRegistryValue(Block.STATE_IDS, fullTripWireState);
                info.cancel();
            }

            if(state.isOf(Blocks.WHEAT) && state.get(CropBlock.AGE) == 0) {
                buf.writeRegistryValue(Block.STATE_IDS, wheatStage0State);
                info.cancel();
            }
        }
    }

    public interface BlockStateHandler {
        default int onBlockState(BlockState state, Function<BlockState, Integer> supplier, int defaultValue) {
            if(state.getBlock() instanceof StrawberryBlockState blockStateTransformer) {
                return supplier.apply(blockStateTransformer.transform(state));
            }
            if(state.isOf(Blocks.NOTE_BLOCK)) {
                return supplier.apply(Blocks.NOTE_BLOCK.getDefaultState());
            }
            if(state.isOf(Blocks.TRIPWIRE)) {
                return supplier.apply(fullTripWireState);
            }

            return defaultValue;
        }
    }

    public interface ItemStackHandler {
        default void onItemStack(ItemStack stack) {
            if(stack.isOf(Items.STRING)) {
                stack.getOrCreateNbt().put("BlockStateTag", GlobalUtils.create(NbtCompound::new, tag -> {
                    tag.putString("east", "true");
                    tag.putString("north", "true");
                    tag.putString("south", "true");
                    tag.putString("west", "true");
                    tag.putString("disarmed", "true");
                    tag.putString("attached", "true");
                    tag.putString("powered", "true");
                }));
            }
        }
    }
}
