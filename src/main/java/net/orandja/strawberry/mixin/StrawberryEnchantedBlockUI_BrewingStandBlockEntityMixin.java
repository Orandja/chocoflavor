package net.orandja.strawberry.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.math.BlockPos;
import net.orandja.strawberry.StrawberryEnchantedBlockUI;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = {
        BrewingStandBlockEntity.class
})
public abstract class StrawberryEnchantedBlockUI_BrewingStandBlockEntityMixin
        extends LockableContainerBlockEntity
        implements StrawberryEnchantedBlockUI.EnchantedSideUI {

    protected StrawberryEnchantedBlockUI_BrewingStandBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
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
