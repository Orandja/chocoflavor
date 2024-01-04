package net.orandja.chocoflavor.recipe;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import net.orandja.chocoflavor.utils.InventoryUtils;
import net.orandja.chocoflavor.utils.NBTUtils;

import java.util.Optional;

public class WhitelistedChestRecipe extends ShapelessRecipe {

    public WhitelistedChestRecipe(String string, CraftingRecipeCategory craftingRecipeCategory, ItemStack itemStack, DefaultedList<Ingredient> defaultedList) {
        super(string, craftingRecipeCategory, itemStack, defaultedList);
    }

    @Override
    public boolean matches(RecipeInputInventory craftingInventory, World world) {
        return InventoryUtils.hasListeners(craftingInventory) && InventoryUtils.getCraftingPlayer(craftingInventory) != null && super.matches(craftingInventory, world);
    }

    @Override
    public ItemStack craft(RecipeInputInventory craftingInventory, DynamicRegistryManager dynamicRegistryManager) {
        PlayerEntity player = InventoryUtils.getCraftingPlayer(craftingInventory);
        if (player == null) {
            return ItemStack.EMPTY;
        }

        ItemStack chestOutput = getResult(dynamicRegistryManager).copy();
        Optional<ItemStack> chestInput = craftingInventory.getInputStacks().stream().filter(it -> it.isOf(Items.CHEST)).findFirst();
        if(chestInput.isEmpty()) {
            return ItemStack.EMPTY;
        }

        NbtCompound tag = chestInput.get().getOrCreateNbt().copy();

        NbtList whitelist = NBTUtils.getOrCreate(tag, "whitelist", (key) -> tag.getList(key, NbtElement.STRING_TYPE), NbtList::new);
        Optional<String> first = whitelist.stream().map(NbtElement::asString).filter(player.getUuidAsString()::equals).findFirst();

        if (first.isEmpty()) {
            String uuid = player.getUuidAsString();
            whitelist.add(NbtString.of(uuid));

            NbtCompound display = NBTUtils.getOrCreate(tag, "display", tag::getCompound, NbtCompound::new);
            NbtList lore = NBTUtils.getOrCreate(display, "Lore", key -> display.getList(key, NbtElement.STRING_TYPE), () -> {
                NbtList list = new NbtList();
                list.add(NbtString.of("{\"text\":\"Whitelisted Chest allowed for: \",\"color\":\"green\"}"));
                return list;
            });
            lore.add(NbtString.of("{\"text\":\"— " + player.getName().getString() + "\",\"color\":\"green\"}"));
        }

        chestOutput.setNbt(tag);
        return chestOutput;
    }
}
