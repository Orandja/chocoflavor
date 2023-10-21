package net.orandja.strawberry.mods.moretools.tools;

import net.minecraft.item.*;
import net.orandja.chocoflavor.mods.doubletools.DoubleTools;
import net.orandja.chocoflavor.utils.StackUtils;
import net.orandja.chocoflavor.utils.TextUtils;
import net.orandja.chocoflavor.utils.Utils;
import net.orandja.strawberry.mods.core.intf.StrawberryItem;

public class StrawberryShearsItem extends ShearsItem implements StrawberryItem, DoubleTools.Applicable {

    private final ToolMaterial material;
    private final int customDataModel;

    public StrawberryShearsItem(ToolMaterial toolMaterial, int customDataModel) {
        super(new Item.Settings().maxDamage(toolMaterial.getDurability()));
        this.customDataModel = customDataModel;
        this.material = toolMaterial;
    }

    @Override
    public ItemStack transform(ItemStack sourceStack) {
        return Utils.apply(transform(sourceStack, Items.SHEARS, this.customDataModel), it -> {
            it.setDamage(StackUtils.convertDurability(sourceStack, it.getMaxDamage()));
            it.addHideFlag(ItemStack.TooltipSection.MODIFIERS);
            TextUtils.addDurability(it.getOrCreateNbt(), sourceStack.getDamage(), sourceStack.getMaxDamage());
        });
    }

    @Override
    public void register() {

    }

    @Override
    public ToolMaterial getMaterial() {
        return this.material;
    }
}
