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
import net.orandja.strawberry.item.StrawberryBlockItem;
import net.orandja.strawberry.item.StrawberryItem;
import net.orandja.strawberry.item.StrawberrySeedItem;
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
        RICE_CROP = Blocks.register("rice_crop", new SimpleCrop(2, () -> RICE_SEEDS));
        CABBAGE_CROP = Blocks.register("cabbage_crop", new SimpleCrop(6, () -> CABBAGE_SEEDS));
        ONION_CROP = Blocks.register("onion_crop", new SimpleCrop(10, () -> ONION_SEEDS));
        TOMATO_CROP = Blocks.register("tomato_crop", new SimpleCrop(14, () -> TOMATO_SEEDS));

        //Seeds
        RICE_SEEDS = Items.register("rice_seeds", new StrawberrySeedItem(RICE_CROP, 1, new Item.Settings()));
        CABBAGE_SEEDS = Items.register("cabbage_seeds", new StrawberrySeedItem(CABBAGE_CROP, 5, new Item.Settings()));
        ONION_SEEDS = Items.register("onion_seeds", new StrawberrySeedItem(ONION_CROP, 9, new Item.Settings()));
        TOMATO_SEEDS = Items.register("tomato_seeds", new StrawberrySeedItem(TOMATO_CROP, 13, new Item.Settings()));

        //Ingredients
        RICE = Items.register("rice", new StrawberryItem(Items.WHEAT, 1, new Item.Settings()));
        COOKED_RICE = Items.register("cooked_rice", new StrawberryItem(Items.BAKED_POTATO, 1, new Item.Settings().food(FoodComponents.BAKED_POTATO)));

        CABBAGE = Items.register("cabbage", new StrawberryItem(Items.WHEAT, 2, new Item.Settings()));
        CABBAGE_LEAF = Items.register("cabbage_leaf", new StrawberryItem(Items.BAKED_POTATO, 2, new Item.Settings()));
        ONION = Items.register("onion", new StrawberryItem(Items.BAKED_POTATO, 3, new Item.Settings()));
        MINCED_BEEF = Items.register("minced_beef", new StrawberryItem(Items.BEEF, 1, new Item.Settings()));
        MINCED_PORK = Items.register("minced_pork", new StrawberryItem(Items.PORKCHOP, 1, new Item.Settings()));
        RAW_BACON = Items.register("raw_bacon", new StrawberryItem(Items.PORKCHOP, 2, new Item.Settings()));
        TOMATO = Items.register("tomato", new StrawberryItem(Items.BAKED_POTATO, 4, new Item.Settings()));

        // Meals
        OYAKODON = Items.register("oyakodon", new StrawberryItem(Items.BEETROOT_SOUP, 1, new Item.Settings().maxCount(1).food(FoodComponents.BEETROOT_SOUP)));

        //Blocks
        BOILER = Blocks.register("boiler", new BoilerBlock());
        BOILER_ENTITY = createBlockEntity("boiler", BlockEntityType.Builder.create(BoilerBlockEntity::new, BOILER));
        BOILER_ITEM = Items.register(new StrawberryBlockItem(BOILER, 17, new Item.Settings()));
    }

    private static <T extends BlockEntity> BlockEntityType<T> createBlockEntity(String id, BlockEntityType.Builder<T> builder) {
        Type<?> type = Util.getChoiceType(TypeReferences.BLOCK_ENTITY, id);
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, id, builder.build(type));
    }
}
