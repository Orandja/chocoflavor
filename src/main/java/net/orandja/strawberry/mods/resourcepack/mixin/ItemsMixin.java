package net.orandja.strawberry.mods.resourcepack.mixin;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.orandja.chocoflavor.ChocoFlavor;
import net.orandja.strawberry.mods.core.intf.StrawberryItem;
import net.orandja.strawberry.mods.resourcepack.StrawberryResourcePackGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Items.class)
public abstract class ItemsMixin {

    @Inject(method = "register(Ljava/lang/String;Lnet/minecraft/item/Item;)Lnet/minecraft/item/Item;", at = @At("TAIL"))
    private static void register(String id, Item item, CallbackInfoReturnable<Item> cir) {
        if(item instanceof StrawberryItem transformer) {
            transformer.register();
            StrawberryResourcePackGenerator.save();
        }
    }

}
