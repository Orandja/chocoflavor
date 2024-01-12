package net.orandja.strawberry.recipe;

import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import net.orandja.chocoflavor.utils.InventoryUtils;
import net.orandja.strawberry.item.ChargedIngotItem;

public class ChargedIngotShapedRecipe extends ShapedRecipe {
    public ChargedIngotShapedRecipe(String group, CraftingRecipeCategory category, int width, int height, DefaultedList<Ingredient> ingredients, ItemStack result) {
        super(group, category, width, height, ingredients, result);
    }

    @Override
    public boolean matches(RecipeInputInventory craftingInventory, World world) {
        return super.matches(craftingInventory, world) && InventoryUtils.toStream(craftingInventory).noneMatch(ChargedIngotItem::isInvalidChargedIngot);
    }
}
