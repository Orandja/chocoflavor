package net.orandja.strawberry.item;

import lombok.Getter;
import net.minecraft.item.*;
import net.orandja.strawberry.intf.StrawberryToolItem;
import net.orandja.strawberry.material.StrawberryToolMaterial;

public class StrawberrySwordItem extends SwordItem implements StrawberryToolItem {

    @Getter
    private final int customDataModel;
    @Getter
    private final SwordItem replacementItem;

    public StrawberrySwordItem(StrawberryToolMaterial material, Item replacementItem, int customDataModel) {
        this(material, ((SwordItem)replacementItem), customDataModel, new Item.Settings());
    }

    public StrawberrySwordItem(ToolMaterial material, SwordItem replacementItem, int customDataModel, Settings settings) {
        this(material, replacementItem, customDataModel, StrawberryToolItem.getBaseAttackDamage(replacementItem), StrawberryToolItem.getAttackSpeed(replacementItem), settings);
    }

    public StrawberrySwordItem(ToolMaterial material, SwordItem replacementItem, int customDataModel, int damage, float attackSpeed, Settings settings) {
        super(material, damage, attackSpeed, settings);
        this.customDataModel = customDataModel;
        this.replacementItem = replacementItem;
    }
}
