package net.orandja.chocoflavor.mods.core.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.screen.slot.Slot;
import net.orandja.chocoflavor.mods.core.CustomRecipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CraftingResultSlot.class)
public abstract class CraftingResultSlotMixin extends Slot {
    @Shadow @Final private RecipeInputInventory input;

    @Shadow @Final private PlayerEntity player;
    private RecipeEntry<CraftingRecipe> recipe = null;

    public CraftingResultSlotMixin(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Redirect(method = "onTakeItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/RecipeInputInventory;removeStack(II)Lnet/minecraft/item/ItemStack;"))
    public ItemStack onTakeItem(RecipeInputInventory instance, int slot, int amount) {
        return CustomRecipe.interceptOnTakeItem(recipe, input, this.player, slot, amount);
    }

    @Inject(method = "onTakeItem", at = @At("HEAD"))
    public void onTakeItemHead(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        this.recipe = player.getWorld().getRecipeManager().getFirstMatch(RecipeType.CRAFTING, input, player.getWorld()).orElse(null);
    }

}