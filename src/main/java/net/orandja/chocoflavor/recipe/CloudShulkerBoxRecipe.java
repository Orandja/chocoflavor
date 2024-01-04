package net.orandja.chocoflavor.recipe;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import net.orandja.chocoflavor.ChocoShulkerBoxes;
import net.orandja.chocoflavor.utils.InventoryUtils;
import net.orandja.chocoflavor.utils.NBTUtils;
import net.orandja.chocoflavor.utils.StackUtils;

import java.util.Optional;

public class CloudShulkerBoxRecipe extends ShapelessRecipe {

    public CloudShulkerBoxRecipe(String group, CraftingRecipeCategory category, ItemStack itemStack, DefaultedList<Ingredient> defaultedList) {
        super(group, category, itemStack, defaultedList);
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return super.isIgnoredInRecipeBook();
    }

    @Override
    public ItemStack getResult(DynamicRegistryManager dynamicRegistryManager) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean matches(RecipeInputInventory craftingInventory, World world) {
        return super.matches(craftingInventory, world) && InventoryUtils.toStream(craftingInventory).anyMatch(ChocoShulkerBoxes.Utils::isValidNamedEmptyShulkerBox);
    }

    @Override
    public ItemStack craft(RecipeInputInventory craftingInventory, DynamicRegistryManager dynamicRegistryManager) {
        Optional<ItemStack> optionalOutput = InventoryUtils.toStream(craftingInventory).filter(ChocoShulkerBoxes.Utils::hasValidChannelName).findFirst();
        if(optionalOutput.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack output = optionalOutput.get().copy();

        String channel = output.getName().getString();
        String channelLiteral;
        if(channel.startsWith(":")) {
            PlayerEntity player = InventoryUtils.getCraftingPlayer(craftingInventory);
            channel = channel.substring(1);
            channelLiteral = channel + " of " + player.getEntityName();
            channel = player.getUuidAsString() + ":" + channel;
        } else {
            channelLiteral = channel + " of public";
            channel = "public:" + channel;
        }

        output.setCustomName(Text.literal("[Cloud Box]").formatted(Formatting.GREEN));
        NbtCompound tag = output.getOrCreateNbt();
        tag.putString("vw_channel", channel);
        tag.putString("vw_channel_literal", channelLiteral);
        NBTUtils.getOrCreate(tag, "Enchantments", key -> tag.getList(key, NbtElement.COMPOUND_TYPE), NbtList::new);
        StackUtils.computeLore(output, lore -> {
            lore.add(NbtString.of("{\"text\":\""+ channelLiteral +"\", \"color\":\"blue\"}"));
        });

        return output;
    }
}
