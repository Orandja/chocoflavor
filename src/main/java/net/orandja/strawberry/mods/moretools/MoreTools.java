package net.orandja.strawberry.mods.moretools;

import net.fabricmc.yarn.constants.MiningLevels;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.Instrument;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Util;
import net.orandja.chocoflavor.mods.core.EnchantMore;
import net.orandja.chocoflavor.utils.NBTUtils;
import net.orandja.chocoflavor.utils.TextUtils;
import net.orandja.chocoflavor.utils.Utils;
import net.orandja.strawberry.mods.core.block.StrawberryBlock;
import net.orandja.strawberry.mods.core.item.SimpleBlockItem;
import net.orandja.strawberry.mods.moretools.tools.*;

import java.util.EnumMap;
import java.util.function.Consumer;

public class MoreTools {

    public static CustomToolMaterial COPPER_TOOL_MATERIAL = new CustomToolMaterial(MiningLevels.STONE, 200, 6.0f, 2.0f, 14, () -> Ingredient.ofItems(Items.COPPER_INGOT));
    public static CustomToolMaterial DEEPSLATE_TOOL_MATERIAL = new CustomToolMaterial(MiningLevels.STONE, 131, 6.0f, 2.0f, 14, () -> Ingredient.ofItems(Items.COBBLED_DEEPSLATE));
    public static CustomToolMaterial EMERALD_TOOL_MATERIAL = new CustomToolMaterial(MiningLevels.IRON, 800, 12.0f, 0.0f, 22, () -> Ingredient.ofItems(Items.EMERALD));
    public static CustomToolMaterial OBSIDIAN_TOOL_MATERIAL = new CustomToolMaterial(MiningLevels.DIAMOND, 3000, 8.0f, 0.0f, -1, () -> Ingredient.ofItems(Items.OBSIDIAN))
            .setEnchantingCheck((enchantment, stack) -> false);
    public static CustomToolMaterial CRYING_OBSIDIAN_TOOL_MATERIAL = new CustomToolMaterial(MiningLevels.DIAMOND, 6000, 9.0f, 0.0f, -1, () -> Ingredient.ofItems(Items.CRYING_OBSIDIAN))
            .setEnchantingCheck((enchantment, stack) -> enchantment.equals(Enchantments.EFFICIENCY) && EnchantmentHelper.getLevel(enchantment, stack) <= 5);
    public static CustomToolMaterial REINFORCED_OBSIDIAN_TOOL_MATERIAL = new CustomToolMaterial(MiningLevels.NETHERITE, 15000, 9.0f + ( 1 + ( 5 * 5)), 0.0f, -1, () -> Ingredient.ofItems(Items.OBSIDIAN))
            .setEnchantingCheck((enchantment, stack) -> !Utils.anyEquals(enchantment, Enchantments.UNBREAKING, Enchantments.EFFICIENCY));

    public static CustomArmorMaterial COPPER_ARMOR_MATERIAL = new CustomArmorMaterial("copper", 10, Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
        map.put(ArmorItem.Type.BOOTS, 2);
        map.put(ArmorItem.Type.LEGGINGS, 5);
        map.put(ArmorItem.Type.CHESTPLATE, 6);
        map.put(ArmorItem.Type.HELMET, 2);
    }), 25, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0f, 0.0f, () -> Ingredient.ofItems(Items.COPPER_INGOT));

    public static CustomArmorMaterial EMERALD_ARMOR_MATERIAL = new CustomArmorMaterial("emerald", 22, Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
        map.put(ArmorItem.Type.BOOTS, 2);
        map.put(ArmorItem.Type.LEGGINGS, 5);
        map.put(ArmorItem.Type.CHESTPLATE, 6);
        map.put(ArmorItem.Type.HELMET, 2);
    }), 25, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0f, 0.0f, () -> Ingredient.ofItems(Items.EMERALD));

    public static Item COPPER_PICKAXE;
    public static Item COPPER_AXE;
    public static Item COPPER_HOE;
    public static Item COPPER_SHOVEL;
    public static Item COPPER_SWORD;
    public static Item COPPER_HELMET;
    public static Item COPPER_CHESTPLATE;
    public static Item COPPER_LEGGINGS;
    public static Item COPPER_BOOTS;

    public static Item DEEPSLATE_PICKAXE;
    public static Item DEEPSLATE_AXE;
    public static Item DEEPSLATE_HOE;
    public static Item DEEPSLATE_SHOVEL;
    public static Item DEEPSLATE_SWORD;

    public static Item EMERALD_PICKAXE;
    public static Item EMERALD_AXE;
    public static Item EMERALD_HOE;
    public static Item EMERALD_SHOVEL;
    public static Item EMERALD_SWORD;
    public static Item EMERALD_HELMET;
    public static Item EMERALD_CHESTPLATE;
    public static Item EMERALD_LEGGINGS;
    public static Item EMERALD_BOOTS;

    public static Item OBSIDIAN_PICKAXE;
    public static Item OBSIDIAN_AXE;
    public static Item OBSIDIAN_HOE;
    public static Item OBSIDIAN_SHOVEL;
    public static Item OBSIDIAN_SWORD;
    public static Item OBSIDIAN_HELMET;
    public static Item OBSIDIAN_CHESTPLATE;
    public static Item OBSIDIAN_LEGGINGS;
    public static Item OBSIDIAN_BOOTS;

    public static Item CRYING_OBSIDIAN_PICKAXE;
    public static Item CRYING_OBSIDIAN_AXE;
    public static Item CRYING_OBSIDIAN_HOE;
    public static Item CRYING_OBSIDIAN_SHOVEL;
    public static Item CRYING_OBSIDIAN_SWORD;
    public static Item CRYING_OBSIDIAN_HELMET;
    public static Item CRYING_OBSIDIAN_CHESTPLATE;
    public static Item CRYING_OBSIDIAN_LEGGINGS;
    public static Item CRYING_OBSIDIAN_BOOTS;

    public static Block REINFORCED_OBSIDIAN;
    public static Item REINFORCED_OBSIDIAN_ITEM;
    public static Item REINFORCED_OBSIDIAN_PICKAXE;
    public static Item REINFORCED_OBSIDIAN_AXE;
    public static Item REINFORCED_OBSIDIAN_HOE;
    public static Item REINFORCED_OBSIDIAN_SHOVEL;
    public static Item REINFORCED_OBSIDIAN_SWORD;
    public static Consumer<ItemStack> REINFORCED_OBSIDIAN_TOOL_EFFICIENCY = it -> {
        it.addHideFlag(ItemStack.TooltipSection.ENCHANTMENTS);
        TextUtils.addEnchantments(it.getOrCreateNbt(), it.getEnchantments());
        it.addEnchantment(Enchantments.EFFICIENCY, EnchantmentHelper.getLevel(Enchantments.EFFICIENCY, it) + 5);
        NBTUtils.addToLore(it.getOrCreateNbt(), TextUtils.getNonItalicTranslatable("minecraft.lore.emptyline"));
    };
    public static Item REINFORCED_OBSIDIAN_HELMET;
    public static Item REINFORCED_OBSIDIAN_CHESTPLATE;
    public static Item REINFORCED_OBSIDIAN_LEGGINGS;
    public static Item REINFORCED_OBSIDIAN_BOOTS;

    public static void beforeLaunch() {
        //Bucket

        COPPER_AXE = Items.register("copper_axe", new StrawberryAxeItem(COPPER_TOOL_MATERIAL, Items.STONE_AXE, 1));
        COPPER_PICKAXE = Items.register("copper_pickaxe", new StrawberryPickaxeItem(COPPER_TOOL_MATERIAL, Items.STONE_PICKAXE, 1));
        COPPER_HOE = Items.register("copper_hoe", new StrawberryHoeItem(COPPER_TOOL_MATERIAL, Items.STONE_HOE, 1));
        COPPER_SHOVEL = Items.register("copper_shovel", new StrawberryShovelItem(COPPER_TOOL_MATERIAL, Items.STONE_SHOVEL, 1));
        COPPER_SWORD = Items.register("copper_sword", new StrawberrySwordItem(COPPER_TOOL_MATERIAL, Items.STONE_SWORD, 1));
        COPPER_HELMET = Items.register("copper_helmet", new StrawberryArmorItem(COPPER_ARMOR_MATERIAL, Items.LEATHER_HELMET, 1, ArmorItem.Type.HELMET, new Item.Settings()));
        COPPER_CHESTPLATE = Items.register("copper_chestplate", new StrawberryArmorItem(COPPER_ARMOR_MATERIAL, Items.LEATHER_CHESTPLATE, 1, ArmorItem.Type.CHESTPLATE, new Item.Settings()));
        COPPER_LEGGINGS = Items.register("copper_leggings", new StrawberryArmorItem(COPPER_ARMOR_MATERIAL, Items.LEATHER_LEGGINGS, 1, ArmorItem.Type.LEGGINGS, new Item.Settings()));
        COPPER_BOOTS = Items.register("copper_boots", new StrawberryArmorItem(COPPER_ARMOR_MATERIAL, Items.LEATHER_BOOTS, 1, ArmorItem.Type.BOOTS, new Item.Settings()));

        DEEPSLATE_PICKAXE = Items.register("deepslate_pickaxe", new StrawberryPickaxeItem(DEEPSLATE_TOOL_MATERIAL, Items.STONE_PICKAXE, 2));
        DEEPSLATE_AXE = Items.register("deepslate_axe", new StrawberryAxeItem(DEEPSLATE_TOOL_MATERIAL, Items.STONE_AXE, 2));
        DEEPSLATE_HOE = Items.register("deepslate_hoe", new StrawberryHoeItem(DEEPSLATE_TOOL_MATERIAL, Items.STONE_HOE, 2));
        DEEPSLATE_SHOVEL = Items.register("deepslate_shovel", new StrawberryShovelItem(DEEPSLATE_TOOL_MATERIAL, Items.STONE_SHOVEL, 2));
        DEEPSLATE_SWORD = Items.register("deepslate_sword", new StrawberrySwordItem(DEEPSLATE_TOOL_MATERIAL, Items.STONE_SWORD, 2));

        EMERALD_PICKAXE = Items.register("emerald_pickaxe", new StrawberryPickaxeItem(EMERALD_TOOL_MATERIAL, Items.GOLDEN_PICKAXE, 1));
        EMERALD_AXE = Items.register("emerald_axe", new StrawberryAxeItem(EMERALD_TOOL_MATERIAL, Items.GOLDEN_AXE, 1));
        EMERALD_HOE = Items.register("emerald_hoe", new StrawberryHoeItem(EMERALD_TOOL_MATERIAL, Items.GOLDEN_HOE, 1));
        EMERALD_SHOVEL = Items.register("emerald_shovel", new StrawberryShovelItem(EMERALD_TOOL_MATERIAL, Items.GOLDEN_SHOVEL, 1));
        EMERALD_SWORD = Items.register("emerald_sword", new StrawberrySwordItem(EMERALD_TOOL_MATERIAL, Items.GOLDEN_SWORD, 1));
        EMERALD_HELMET = Items.register("emerald_helmet", new StrawberryArmorItem(EMERALD_ARMOR_MATERIAL, Items.LEATHER_HELMET, 2, ArmorItem.Type.HELMET, new Item.Settings()));
        EMERALD_CHESTPLATE = Items.register("emerald_chestplate", new StrawberryArmorItem(EMERALD_ARMOR_MATERIAL, Items.LEATHER_CHESTPLATE, 2, ArmorItem.Type.CHESTPLATE, new Item.Settings()));
        EMERALD_LEGGINGS = Items.register("emerald_leggings", new StrawberryArmorItem(EMERALD_ARMOR_MATERIAL, Items.LEATHER_LEGGINGS, 2, ArmorItem.Type.LEGGINGS, new Item.Settings()));
        EMERALD_BOOTS = Items.register("emerald_boots", new StrawberryArmorItem(EMERALD_ARMOR_MATERIAL, Items.LEATHER_BOOTS, 2, ArmorItem.Type.BOOTS, new Item.Settings()));

        OBSIDIAN_PICKAXE = Items.register("obsidian_pickaxe", new StrawberryPickaxeItem(OBSIDIAN_TOOL_MATERIAL, Items.DIAMOND_PICKAXE, 1));
        OBSIDIAN_AXE = Items.register("obsidian_axe", new StrawberryAxeItem(OBSIDIAN_TOOL_MATERIAL, Items.DIAMOND_AXE, 1));
        OBSIDIAN_HOE = Items.register("obsidian_hoe", new StrawberryHoeItem(OBSIDIAN_TOOL_MATERIAL, Items.DIAMOND_HOE, 1));
        OBSIDIAN_SHOVEL = Items.register("obsidian_shovel", new StrawberryShovelItem(OBSIDIAN_TOOL_MATERIAL, Items.DIAMOND_SHOVEL, 1));
        OBSIDIAN_SWORD = Items.register("obsidian_sword", new StrawberrySwordItem(OBSIDIAN_TOOL_MATERIAL, Items.DIAMOND_SWORD, 1));

        CRYING_OBSIDIAN_PICKAXE = Items.register("crying_obsidian_pickaxe", new StrawberryPickaxeItem(CRYING_OBSIDIAN_TOOL_MATERIAL, Items.DIAMOND_PICKAXE, 2));
        CRYING_OBSIDIAN_AXE = Items.register("crying_obsidian_axe", new StrawberryAxeItem(CRYING_OBSIDIAN_TOOL_MATERIAL, Items.DIAMOND_AXE, 2));
        CRYING_OBSIDIAN_HOE = Items.register("crying_obsidian_hoe", new StrawberryHoeItem(CRYING_OBSIDIAN_TOOL_MATERIAL, Items.DIAMOND_HOE, 2));
        CRYING_OBSIDIAN_SHOVEL = Items.register("crying_obsidian_shovel", new StrawberryShovelItem(CRYING_OBSIDIAN_TOOL_MATERIAL, Items.DIAMOND_SHOVEL, 2));
        CRYING_OBSIDIAN_SWORD = Items.register("crying_obsidian_sword", new StrawberrySwordItem(CRYING_OBSIDIAN_TOOL_MATERIAL, Items.DIAMOND_SWORD, 2));

        REINFORCED_OBSIDIAN_PICKAXE = Items.register("reinforced_obsidian_pickaxe", Utils.apply(new StrawberryPickaxeItem(REINFORCED_OBSIDIAN_TOOL_MATERIAL, Items.NETHERITE_PICKAXE, 1), it -> it.addCustomTransformer(REINFORCED_OBSIDIAN_TOOL_EFFICIENCY)));
        REINFORCED_OBSIDIAN_AXE = Items.register("reinforced_obsidian_axe", Utils.apply(new StrawberryAxeItem(REINFORCED_OBSIDIAN_TOOL_MATERIAL, Items.NETHERITE_AXE, 1), it -> it.addCustomTransformer(REINFORCED_OBSIDIAN_TOOL_EFFICIENCY)));
        REINFORCED_OBSIDIAN_HOE = Items.register("reinforced_obsidian_hoe", new StrawberryHoeItem(REINFORCED_OBSIDIAN_TOOL_MATERIAL, Items.NETHERITE_HOE, 1));
        REINFORCED_OBSIDIAN_SHOVEL = Items.register("reinforced_obsidian_shovel", Utils.apply(new StrawberryShovelItem(REINFORCED_OBSIDIAN_TOOL_MATERIAL, Items.NETHERITE_SHOVEL, 1), it -> it.addCustomTransformer(REINFORCED_OBSIDIAN_TOOL_EFFICIENCY)));
        REINFORCED_OBSIDIAN_SWORD = Items.register("reinforced_obsidian_sword", new StrawberrySwordItem(REINFORCED_OBSIDIAN_TOOL_MATERIAL, Items.NETHERITE_SWORD, 1));

        REINFORCED_OBSIDIAN = Blocks.register("reinforced_obsidian", new StrawberryBlock(24, "reinforced_obsidian", it -> {
            it.mapColor(MapColor.BLACK).instrument(Instrument.BASEDRUM).requiresTool().strength(150.0f, 1200.0f);
        }));
        REINFORCED_OBSIDIAN_ITEM = Items.register(new SimpleBlockItem(REINFORCED_OBSIDIAN, 24, new Item.Settings()));
    }
}
