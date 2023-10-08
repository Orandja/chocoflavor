package net.orandja.chocoflavor.mods.core.mixin;

import com.google.gson.JsonObject;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.util.Identifier;
import net.orandja.chocoflavor.mods.core.CustomRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin {

    @Inject(at = @At("RETURN"), method = "deserialize", cancellable = true)
    private static void deserialize(Identifier identifier, JsonObject json, CallbackInfoReturnable<RecipeEntry<Recipe<?>>> info) {
        CustomRecipe.getCustomRecipe(identifier, info.getReturnValue(), recipe -> {
            info.setReturnValue(new RecipeEntry<Recipe<?>>(identifier, recipe));
        });
    }

}
