package net.orandja.strawberry.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.math.BlockPos;
import net.orandja.strawberry.StrawberryChestBlockUI;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = {
        ChestBlockEntity.class,
})
public abstract class StrawberryChestUI_ChestBlockEntityMixin
        extends LockableContainerBlockEntity
        implements StrawberryChestBlockUI.WhitelistedChestUI {

    protected StrawberryChestUI_ChestBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Override
    public String getDefaultTranslationKey() {
        return this.getContainerName().getContent() instanceof TranslatableTextContent content ? content.getKey() : "";
    }
    @Override
    public TitlePosition getTitlePosition() {
        return TitlePosition.LEFT;
    }
}
