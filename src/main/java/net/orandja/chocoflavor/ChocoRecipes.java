package net.orandja.chocoflavor;

import com.google.common.collect.Maps;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.orandja.chocoflavor.utils.Suppliers;

import java.util.Map;
import java.util.function.Consumer;

public class ChocoRecipes {

    private static Map<Identifier, Suppliers.Six<ShapedRecipe, String, CraftingRecipeCategory, Integer, Integer, DefaultedList<Ingredient>, ItemStack>> customShapedRecipes = Maps.newHashMap();
    private static Map<Identifier, Suppliers.Quad<ShapelessRecipe, String, CraftingRecipeCategory, ItemStack, DefaultedList<Ingredient>>> customShapelessRecipes = Maps.newHashMap();

    public static void addShapedRecipe(Identifier identifier, Suppliers.Six<ShapedRecipe, String, CraftingRecipeCategory, Integer, Integer, DefaultedList<Ingredient>, ItemStack> recipe) {
        customShapedRecipes.put(identifier, recipe);
    }
    public static void addShapelessRecipe(Identifier identifier, Suppliers.Quad<ShapelessRecipe, String, CraftingRecipeCategory, ItemStack, DefaultedList<Ingredient>> recipe) {
        customShapelessRecipes.put(identifier, recipe);
    }

    public static boolean hasCustomRecipe(Identifier identifier) {
        return customShapelessRecipes.containsKey(identifier) || customShapedRecipes.containsKey(identifier);
    }

    public static void getCustomRecipeEntry(Identifier identifier, RecipeEntry<Recipe<?>> recipeEntry, Consumer<RecipeEntry<Recipe<?>>> consumer) {
        if(customShapedRecipes.containsKey(identifier) && recipeEntry.value() instanceof ShapedRecipe recipe) {
            consumer.accept(new RecipeEntry<>(identifier,
                    customShapedRecipes.get(identifier)
                            .get(recipe.getGroup(), recipe.getCategory(), recipe.getWidth(), recipe.getHeight(), recipe.getIngredients(), recipe.getResult(null))));
            return;
        }

        if(customShapelessRecipes.containsKey(identifier) && recipeEntry.value() instanceof ShapelessRecipe recipe) {
            consumer.accept(new RecipeEntry<>(identifier,
                    customShapelessRecipes.get(identifier).get(recipe.getGroup(), recipe.getCategory(), recipe.getResult(null), recipe.getIngredients())));
        }
    }

    public static ItemStack interceptOnTakeItem(RecipeEntry<CraftingRecipe> recipeEntry, RecipeInputInventory input, PlayerEntity player, int slot, int amount) {
        if(recipeEntry.value() instanceof ResultInterceptor resultInterceptor) {
            return resultInterceptor.onTakeItem(input, player, slot, amount);
        }

        return input.removeStack(slot, amount);
    }

    public interface ResultInterceptor {
        ItemStack onTakeItem(RecipeInputInventory input, PlayerEntity player, int slot, int amount);
    }
}
