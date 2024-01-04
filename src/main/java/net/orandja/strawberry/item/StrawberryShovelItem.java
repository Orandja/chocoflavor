package net.orandja.strawberry.item;

import lombok.Getter;
import net.minecraft.item.*;
import net.orandja.strawberry.intf.StrawberryToolItem;
import net.orandja.strawberry.material.StrawberryToolMaterial;

public class StrawberryShovelItem extends ShovelItem implements StrawberryToolItem {

    @Getter
    private final int customDataModel;
    @Getter
    private final ShovelItem replacementItem;

    public StrawberryShovelItem(StrawberryToolMaterial material, Item replacementItem, int customDataModel) {
        this(material, ((ShovelItem)replacementItem), customDataModel, new Item.Settings());
    }

    public StrawberryShovelItem(ToolMaterial material, ShovelItem replacementItem, int customDataModel, Settings settings) {
        this(material, replacementItem, customDataModel, StrawberryToolItem.getBaseAttackDamage(replacementItem), StrawberryToolItem.getAttackSpeed(replacementItem), settings);
    }

    public StrawberryShovelItem(ToolMaterial material, ShovelItem replacementItem, int customDataModel, int damage, float attackSpeed, Settings settings) {
        super(material, damage, attackSpeed, settings);
        this.customDataModel = customDataModel;
        this.replacementItem = replacementItem;
    }
}
