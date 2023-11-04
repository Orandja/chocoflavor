package net.orandja.strawberry.mods.moretools.tools;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.orandja.chocoflavor.utils.Utils;
import net.orandja.strawberry.mods.core.intf.StrawberryItem;
import net.orandja.strawberry.mods.moretools.CustomToolMaterial;

public class StrawberryPickaxeItem extends PickaxeItem implements StrawberryItem {

    private final int customDataModel;
    private final PickaxeItem replacementItem;

    public StrawberryPickaxeItem(CustomToolMaterial material, Item replacementItem, int customDataModel) {
        this(material, ((PickaxeItem)replacementItem), customDataModel, new Item.Settings());
    }

    public StrawberryPickaxeItem(ToolMaterial material, PickaxeItem replacementItem, int customDataModel, Settings settings) {
        this(material, replacementItem, customDataModel, getBaseAttackDamage(replacementItem), getAttackSpeed(replacementItem), settings);
    }

    public StrawberryPickaxeItem(ToolMaterial material, PickaxeItem replacementItem, int customDataModel, int damage, float attackSpeed, Settings settings) {
        super(material, damage, attackSpeed, settings);
        this.customDataModel = customDataModel;
        this.replacementItem = replacementItem;
    }

    @Override
    public ItemStack transform(ItemStack sourceStack) {
        return Utils.apply(transform(sourceStack, this.replacementItem, this.customDataModel), it -> {
            if(this.getMaterial() instanceof CustomToolMaterial material) {
                material.modifyStack(it, this.replacementItem);
            }
        });
    }

    @Override
    public void register() {
        String path = Registries.ITEM.getId(this).getPath();
        register(this.replacementItem, this.customDataModel, path, "tools/" + path);
    }

    public static int getBaseAttackDamage(MiningToolItem pickaxeItem) {
        return (int) (pickaxeItem.getAttackDamage() - pickaxeItem.getMaterial().getAttackDamage());
    }

    public static float getAttackSpeed(MiningToolItem pickaxeItem) {
        return (float) ((ImmutableList<EntityAttributeModifier>)pickaxeItem.getAttributeModifiers(EquipmentSlot.MAINHAND).get(EntityAttributes.GENERIC_ATTACK_SPEED)).get(0).getValue();
    }

    @Override
    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        return super.getMiningSpeedMultiplier(stack, state);
    }
}
