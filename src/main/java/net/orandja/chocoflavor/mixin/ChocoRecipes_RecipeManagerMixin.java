package net.orandja.chocoflavor.mixin;

import com.google.gson.JsonObject;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.util.Identifier;
import net.orandja.chocoflavor.ChocoRecipes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeManager.class)
public abstract class ChocoRecipes_RecipeManagerMixin {

    @Inject(at = @At("RETURN"), method = "deserialize", cancellable = true)
    private static void deserialize(Identifier identifier, JsonObject json, CallbackInfoReturnable<RecipeEntry<Recipe<?>>> info) {
        ChocoRecipes.getCustomRecipeEntry(identifier, info.getReturnValue(), info::setReturnValue);
    }

}
