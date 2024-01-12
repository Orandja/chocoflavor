package net.orandja.strawberry.intf;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.SwordItem;
import net.orandja.chocoflavor.utils.GlobalUtils;
import net.orandja.strawberry.material.StrawberryToolMaterial;

public interface StrawberryToolItem extends StrawberryItemHandler {

    Item getReplacementItem();
    int getCustomDataModel();
    Object getMaterial();

    default ItemStack transform(ItemStack sourceStack) {
        return GlobalUtils.apply(transform(sourceStack, getReplacementItem(), getCustomDataModel()), it -> {
            if(getMaterial() instanceof StrawberryToolMaterial material) {
                material.modifyStack(it, sourceStack, getReplacementItem());
            }
        });
    }

    static int getBaseAttackDamage(Item item) {
        if(item instanceof MiningToolItem miningToolItem) {
            return (int) (miningToolItem.getAttackDamage() - miningToolItem.getMaterial().getAttackDamage());
        }

        if(item instanceof SwordItem swordItem) {
            return (int) (swordItem.getAttackDamage() - swordItem.getMaterial().getAttackDamage());
        }

        return 0;
    }

    static float getAttackSpeed(Item item) {
        if(item instanceof MiningToolItem miningToolItem) {
            return (float) ((ImmutableList<EntityAttributeModifier>) miningToolItem.getAttributeModifiers(EquipmentSlot.MAINHAND).get(EntityAttributes.GENERIC_ATTACK_SPEED)).get(0).getValue();
        }

        if(item instanceof SwordItem swordItem) {
            return (float) ((ImmutableList<EntityAttributeModifier>) swordItem.getAttributeModifiers(EquipmentSlot.MAINHAND).get(EntityAttributes.GENERIC_ATTACK_SPEED)).get(0).getValue();
        }
        return 0.0F;
    }

}
