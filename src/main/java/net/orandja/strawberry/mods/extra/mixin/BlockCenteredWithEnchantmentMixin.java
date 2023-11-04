package net.orandja.strawberry.mods.extra.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.*;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.math.BlockPos;
import net.orandja.strawberry.mods.core.gui.SideGUIEnchantments;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = {
        BrewingStandBlockEntity.class,
        AbstractFurnaceBlockEntity.class
})
public abstract class BlockCenteredWithEnchantmentMixin
        extends LockableContainerBlockEntity
        implements SideGUIEnchantments {

    protected BlockCenteredWithEnchantmentMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Override
    public String getDefaultTranslationKey() {
        return this.getContainerName().getContent() instanceof TranslatableTextContent content ? content.getKey() : "";
    }

    @Override
    public TitlePosition getTitlePosition() {
        return TitlePosition.CENTER;
    }
}
