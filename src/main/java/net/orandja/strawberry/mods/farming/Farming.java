package net.orandja.strawberry.mods.farming;

import com.mojang.datafixers.types.Type;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.item.FoodComponents;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Util;
import net.orandja.strawberry.mods.core.item.SimpleBlockItem;
import net.orandja.strawberry.mods.core.item.SimpleCustomItem;
import net.orandja.strawberry.mods.core.item.SimpleSeedItem;
import net.orandja.strawberry.mods.farming.block.BoilerBlock;
import net.orandja.strawberry.mods.farming.block.SimpleCrop;
import net.orandja.strawberry.mods.farming.block.entity.BoilerBlockEntity;

public class Farming {

    //Crops
    public static Block RICE_CROP;
    public static Block CABBAGE_CROP;
    public static Block ONION_CROP;
    public static Block TOMATO_CROP;

    //Seeds
    public static Item RICE_SEEDS;
    public static Item CABBAGE_SEEDS;
    public static Item ONION_SEEDS;
    public static Item TOMATO_SEEDS;

    //Ingredients
    public static Item COOKED_RICE;
    public static Item RICE;

    public static Item CABBAGE;
    public static Item CABBAGE_LEAF;

    public static Item ONION;
    public static Item MINCED_BEEF;
    public static Item MINCED_PORK;

    public static Item RAW_BACON;
    public static Item TOMATO;

    // Meals
    public static Item OYAKODON;
    public static Item CABBAGE_ROLLS;

    //Utilities
    public static Block BOILER;
    public static Item BOILER_ITEM;
    public static BlockEntityType<BoilerBlockEntity> BOILER_ENTITY;


    public static void beforeLaunch() {

        //Crops
        RICE_CROP = Blocks.register("rice_crop", new SimpleCrop(1, () -> RICE_SEEDS));
        CABBAGE_CROP = Blocks.register("cabbage_crop", new SimpleCrop(5, () -> CABBAGE_SEEDS));
        ONION_CROP = Blocks.register("onion_crop", new SimpleCrop(9, () -> ONION_SEEDS));
        TOMATO_CROP = Blocks.register("tomato_crop", new SimpleCrop(13, () -> TOMATO_SEEDS));

        //Seeds
        RICE_SEEDS = Items.register("rice_seeds", new SimpleSeedItem(RICE_CROP, "Rice seeds", Items.WHEAT_SEEDS, 1, new Item.Settings()));
        CABBAGE_SEEDS = Items.register("cabbage_seeds", new SimpleSeedItem(CABBAGE_CROP, "Cabbage seeds", Items.WHEAT_SEEDS, 2, new Item.Settings()));
        ONION_SEEDS = Items.register("onion_seeds", new SimpleSeedItem(ONION_CROP, "Onion seeds", Items.WHEAT_SEEDS, 3, new Item.Settings()));
        TOMATO_SEEDS = Items.register("tomato_seeds", new SimpleSeedItem(TOMATO_CROP, "Tomato seeds", Items.WHEAT_SEEDS, 4, new Item.Settings()));

        //Ingredients
        RICE = Items.register("rice", new SimpleCustomItem("Rice", Items.WHEAT, 1, new Item.Settings()));
        COOKED_RICE = Items.register("cooked_rice", new SimpleCustomItem("Cooked Rice", Items.BAKED_POTATO, 1, new Item.Settings().food(FoodComponents.BAKED_POTATO)));

        CABBAGE = Items.register("cabbage", new SimpleCustomItem("Cabbage", Items.WHEAT, 2, new Item.Settings()));
        CABBAGE_LEAF = Items.register("cabbage_leaf", new SimpleCustomItem("Cabbage Leaf", Items.BAKED_POTATO, 2, new Item.Settings()));
        ONION = Items.register("onion", new SimpleCustomItem("Onion", Items.BAKED_POTATO, 3, new Item.Settings()));
        MINCED_BEEF = Items.register("minced_beef", new SimpleCustomItem("Minced Beef", Items.BEEF, 1, new Item.Settings()));
        MINCED_PORK = Items.register("minced_pork", new SimpleCustomItem("Minced Pork", Items.PORKCHOP, 1, new Item.Settings()));
        RAW_BACON = Items.register("raw_bacon", new SimpleCustomItem("Raw Bacon", Items.PORKCHOP, 2, new Item.Settings()));
        TOMATO = Items.register("tomato", new SimpleCustomItem("Tomato", Items.BAKED_POTATO, 4, new Item.Settings()));

        // Meals
        OYAKODON = Items.register("oyakodon", new SimpleCustomItem("Oyakodon", Items.BEETROOT_SOUP, 1, new Item.Settings().maxCount(1).food(FoodComponents.BEETROOT_SOUP)));

        //Blocks
        BOILER = Blocks.register("boiler", new BoilerBlock());
        BOILER_ENTITY = createBlockEntity("boiler", BlockEntityType.Builder.create(BoilerBlockEntity::new, BOILER));
        BOILER_ITEM = Items.register(new SimpleBlockItem(BOILER, 17, new Item.Settings()));
    }

    private static <T extends BlockEntity> BlockEntityType<T> createBlockEntity(String id, BlockEntityType.Builder<T> builder) {
        Type<?> type = Util.getChoiceType(TypeReferences.BLOCK_ENTITY, id);
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, id, builder.build(type));
    }
}
