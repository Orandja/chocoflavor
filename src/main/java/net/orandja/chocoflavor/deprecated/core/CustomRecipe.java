//package net.orandja.chocoflavor.mods.core;
//
//import com.google.common.collect.Maps;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.inventory.RecipeInputInventory;
//import net.minecraft.item.ItemStack;
//import net.minecraft.recipe.*;
//import net.minecraft.recipe.book.CraftingRecipeCategory;
//import net.minecraft.util.Identifier;
//import net.minecraft.util.collection.DefaultedList;
//
//import java.util.Map;
//import java.util.function.Consumer;
//
//public abstract class CustomRecipe {
//
//    public interface Interceptor {
//        ItemStack onTakeItem(RecipeInputInventory input, PlayerEntity player, int slot, int amount);
//    }
//
////    public interface ShapedRecipeConstructor {
////        ShapedRecipe create(String group, int width, int height, DefaultedList<Ingredient> input, ItemStack output);
////    }
////
////    public interface ShapelessRecipeConstructor {
////        ShapelessRecipe create(String string, CraftingRecipeCategory craftingRecipeCategory, ItemStack itemStack, DefaultedList<Ingredient> defaultedList);
////    }
////
////    public static Map<Identifier, ShapedRecipeConstructor> customShapedRecipes = Maps.newHashMap();
////    public static Map<Identifier, ShapelessRecipeConstructor> customShapelessRecipes = Maps.newHashMap();
////
////    public static boolean hasCustomRecipe(Identifier identifier) {
////        return customShapelessRecipes.containsKey(identifier) || customShapedRecipes.containsKey(identifier);
////    }
////
////    public static void getCustomRecipe(Identifier identifier, RecipeEntry<Recipe<?>> recipeEntry, Consumer<Recipe> consumer) {
////        if(customShapedRecipes.containsKey(identifier) && recipeEntry.value() instanceof ShapedRecipe recipe) {
////            consumer.accept(customShapedRecipes.get(identifier).create(recipe.getGroup(), recipe.getWidth(), recipe.getHeight(), recipe.getIngredients(), recipe.getResult(null)));
////            return;
////        }
////
////        if(customShapelessRecipes.containsKey(identifier) && recipeEntry.value() instanceof ShapelessRecipe recipe) {
////            consumer.accept(customShapelessRecipes.get(identifier).create(recipe.getGroup(), recipe.getCategory(), recipe.getResult(null), recipe.getIngredients()));
////        }
////    }
//
//    public static ItemStack interceptOnTakeItem(RecipeEntry<CraftingRecipe> recipeEntry, RecipeInputInventory input, PlayerEntity player, int slot, int amount) {
//        if(recipeEntry.value() instanceof Interceptor interceptor) {
//            return interceptor.onTakeItem(input, player, slot, amount);
//        }
//
//        return input.removeStack(slot, amount);
//    }
//
//}