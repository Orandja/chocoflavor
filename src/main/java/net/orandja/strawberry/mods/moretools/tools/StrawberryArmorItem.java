package net.orandja.strawberry.mods.moretools.tools;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.orandja.chocoflavor.utils.NBTUtils;
import net.orandja.chocoflavor.utils.StackUtils;
import net.orandja.chocoflavor.utils.TextUtils;
import net.orandja.chocoflavor.utils.Utils;
import net.orandja.strawberry.mods.core.intf.StrawberryItem;
import net.orandja.strawberry.mods.moretools.CustomArmorMaterial;
import net.orandja.strawberry.mods.resourcepack.StrawberryResourcePackGenerator;

public class StrawberryArmorItem extends ArmorItem implements StrawberryItem {

    private final int customDataModel;
    private final ArmorItem replacementItem;

    public StrawberryArmorItem(CustomArmorMaterial material, Item replacementItem, int customDataModel, Type type, Settings settings) {
        this(material, ((ArmorItem)replacementItem), customDataModel, type, settings);
    }

    public StrawberryArmorItem(CustomArmorMaterial material, ArmorItem replacementItem, int customDataModel, Type type, Settings settings) {
        super(material, type, settings);
        this.customDataModel = customDataModel;
        this.replacementItem = replacementItem;
    }

    @Override
    public ItemStack transform(ItemStack sourceStack) {
        return Utils.apply(transform(sourceStack, this.replacementItem, this.customDataModel), it -> {
            it.setDamage(StackUtils.convertDurability(sourceStack, this.replacementItem));
            TextUtils.addDurability(it.getOrCreateNbt(), sourceStack.getDamage(), sourceStack.getMaxDamage());
            it.addHideFlag(ItemStack.TooltipSection.MODIFIERS);
            NBTUtils.getOrCreate(it.getOrCreateNbt(), "display", NbtCompound::new).putInt("color", customDataModel);
            // Hide AttributeModifiers && Dyed (64 + 2)
            // see: https://minecraft.fandom.com/wiki/Tutorials/Command_NBT_tags
            // it.getOrCreateNbt().putInt("HideFlags", 66);
        });
    }

    @Override
    public void register() {
        String path = Registries.ITEM.getId(this).getPath();
        StrawberryResourcePackGenerator.getModelData(Registries.ITEM.getId(replacementItem).getPath()).add(new StrawberryResourcePackGenerator.CustomModelData(this.customDataModel, path, new String[]{ "armor/" + path, "armor/" + path }));
    }
}
