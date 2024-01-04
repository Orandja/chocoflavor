package net.orandja.strawberry.item;

import lombok.Getter;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterial;
import net.orandja.strawberry.intf.StrawberryToolItem;
import net.orandja.strawberry.material.StrawberryToolMaterial;

public class StrawberryAxeItem extends AxeItem implements StrawberryToolItem {

    @Getter
    private final int customDataModel;
    @Getter
    private final AxeItem replacementItem;

    public StrawberryAxeItem(StrawberryToolMaterial material, Item replacementItem, int customDataModel) {
        this(material, ((AxeItem)replacementItem), customDataModel, new Item.Settings());
    }

    public StrawberryAxeItem(ToolMaterial material, AxeItem replacementItem, int customDataModel, Settings settings) {
        this(material, replacementItem, customDataModel, StrawberryToolItem.getBaseAttackDamage(replacementItem), StrawberryToolItem.getAttackSpeed(replacementItem), settings);
    }

    public StrawberryAxeItem(ToolMaterial material, AxeItem replacementItem, int customDataModel, float damage, float attackSpeed, Settings settings) {
        super(material, damage, attackSpeed, settings);
        this.customDataModel = customDataModel;
        this.replacementItem = replacementItem;
    }
}
