//package net.orandja.strawberry.mods.extra.mixin;
//
//import net.minecraft.block.BlockState;
//import net.minecraft.block.entity.BarrelBlockEntity;
//import net.minecraft.block.entity.BlockEntityType;
//import net.minecraft.block.entity.HopperBlockEntity;
//import net.minecraft.block.entity.LockableContainerBlockEntity;
//import net.minecraft.text.TranslatableTextContent;
//import net.minecraft.util.math.BlockPos;
//import net.orandja.strawberry.mods.core.gui.SideGUIDeepStorageBarrel;
//import net.orandja.strawberry.mods.core.gui.SideGUIEnchantments;
//import org.spongepowered.asm.mixin.Mixin;
//
//@Mixin(value = {
//        BarrelBlockEntity.class,
//})
//public abstract class DeepStorageBarrelMixin
//        extends LockableContainerBlockEntity
//        implements SideGUIDeepStorageBarrel {
//
//    protected DeepStorageBarrelMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
//        super(blockEntityType, blockPos, blockState);
//    }
//
//    @Override
//    public String getDefaultTranslationKey() {
//        return this.getContainerName().getContent() instanceof TranslatableTextContent content ? content.getKey() : "";
//    }
//    @Override
//    public TitlePosition getTitlePosition() {
//        return TitlePosition.LEFT;
//    }
//}
