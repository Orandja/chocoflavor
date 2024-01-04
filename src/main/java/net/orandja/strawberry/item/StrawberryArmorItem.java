package net.orandja.strawberry.item;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.orandja.chocoflavor.utils.NBTUtils;
import net.orandja.chocoflavor.utils.StackUtils;
import net.orandja.chocoflavor.utils.TextUtils;
import net.orandja.chocoflavor.utils.GlobalUtils;
import net.orandja.strawberry.intf.StrawberryItemHandler;
import net.orandja.strawberry.material.StrawberryArmorMaterial;

public class StrawberryArmorItem extends ArmorItem implements StrawberryItemHandler {

    private final int customDataModel;
    private final ArmorItem replacementItem;

    public StrawberryArmorItem(StrawberryArmorMaterial material, Item replacementItem, int customDataModel, Type type, Settings settings) {
        this(material, ((ArmorItem)replacementItem), customDataModel, type, settings);
    }

    public StrawberryArmorItem(StrawberryArmorMaterial material, ArmorItem replacementItem, int customDataModel, Type type, Settings settings) {
        super(material, type, settings);
        this.customDataModel = customDataModel;
        this.replacementItem = replacementItem;
    }

    @Override
    public ItemStack transform(ItemStack sourceStack) {
        return GlobalUtils.apply(transform(sourceStack, this.replacementItem, this.customDataModel), it -> {
            it.setDamage(StackUtils.convertDurability(sourceStack, this.replacementItem));
            TextUtils.addDurability(it.getOrCreateNbt(), sourceStack.getDamage(), sourceStack.getMaxDamage());
            it.addHideFlag(ItemStack.TooltipSection.MODIFIERS);
            NBTUtils.getOrCreate(it.getOrCreateNbt(), "display", NbtCompound::new).putInt("color", customDataModel);
            // Hide AttributeModifiers && Dyed (64 + 2)
            // see: https://minecraft.fandom.com/wiki/Tutorials/Command_NBT_tags
            // it.getOrCreateNbt().putInt("HideFlags", 66);
        });
    }
}
