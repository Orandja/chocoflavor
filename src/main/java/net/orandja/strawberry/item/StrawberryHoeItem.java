package net.orandja.strawberry.item;

import lombok.Getter;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterial;
import net.orandja.strawberry.intf.StrawberryToolItem;
import net.orandja.strawberry.material.StrawberryToolMaterial;

public class StrawberryHoeItem extends HoeItem implements StrawberryToolItem {

    @Getter
    private final int customDataModel;
    @Getter
    private final HoeItem replacementItem;

    public StrawberryHoeItem(StrawberryToolMaterial material, Item replacementItem, int customDataModel) {
        this(material, ((HoeItem)replacementItem), customDataModel, new Item.Settings());
    }

    public StrawberryHoeItem(ToolMaterial material, HoeItem replacementItem, int customDataModel, Settings settings) {
        this(material, replacementItem, customDataModel, StrawberryToolItem.getBaseAttackDamage(replacementItem), StrawberryToolItem.getAttackSpeed(replacementItem), settings);
    }

    public StrawberryHoeItem(ToolMaterial material, HoeItem replacementItem, int customDataModel, int damage, float attackSpeed, Settings settings) {
        super(material, damage, attackSpeed, settings);
        this.customDataModel = customDataModel;
        this.replacementItem = replacementItem;
    }
}
