package net.orandja.strawberry.item;

import lombok.Getter;
import net.minecraft.item.*;
import net.orandja.chocoflavor.ChocoTools;
import net.orandja.strawberry.intf.StrawberryToolItem;

public class StrawberryShearsItem extends ShearsItem implements StrawberryToolItem, ChocoTools.MaterialSupplier {

    @Getter
    private final ToolMaterial material;
    @Getter
    private final int customDataModel;

    public StrawberryShearsItem(ToolMaterial toolMaterial, int customDataModel) {
        super(new Item.Settings().maxDamage(toolMaterial.getDurability()));
        this.customDataModel = customDataModel;
        this.material = toolMaterial;
    }

    @Override
    public Item getReplacementItem() {
        return Items.SHEARS;
    }
}
