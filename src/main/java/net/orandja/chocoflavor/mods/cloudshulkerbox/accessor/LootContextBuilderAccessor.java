package net.orandja.chocoflavor.mods.cloudshulkerbox.accessor;

import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameterSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LootContext.Builder.class)
public interface LootContextBuilderAccessor {

    @Accessor
    LootContextParameterSet getParameters();

}
