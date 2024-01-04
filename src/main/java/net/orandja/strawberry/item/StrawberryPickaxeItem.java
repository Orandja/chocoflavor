package net.orandja.strawberry.item;

import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.item.*;
import net.orandja.strawberry.intf.StrawberryToolItem;
import net.orandja.strawberry.material.StrawberryToolMaterial;

public class StrawberryPickaxeItem extends PickaxeItem implements StrawberryToolItem {

    @Getter
    private final int customDataModel;
    @Getter
    private final PickaxeItem replacementItem;

    public StrawberryPickaxeItem(StrawberryToolMaterial material, Item replacementItem, int customDataModel) {
        this(material, ((PickaxeItem)replacementItem), customDataModel, new Item.Settings());
    }

    public StrawberryPickaxeItem(ToolMaterial material, PickaxeItem replacementItem, int customDataModel, Settings settings) {
        this(material, replacementItem, customDataModel, StrawberryToolItem.getBaseAttackDamage(replacementItem), StrawberryToolItem.getAttackSpeed(replacementItem), settings);
    }

    public StrawberryPickaxeItem(ToolMaterial material, PickaxeItem replacementItem, int customDataModel, int damage, float attackSpeed, Settings settings) {
        super(material, damage, attackSpeed, settings);
        this.customDataModel = customDataModel;
        this.replacementItem = replacementItem;
    }

    @Override
    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        return super.getMiningSpeedMultiplier(stack, state);
    }
}
