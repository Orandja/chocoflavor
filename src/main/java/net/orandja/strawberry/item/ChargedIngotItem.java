package net.orandja.strawberry.item;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.orandja.chocoflavor.utils.GlobalUtils;
import net.orandja.chocoflavor.utils.NBTUtils;
import net.orandja.chocoflavor.utils.TextUtils;

public class ChargedIngotItem extends StrawberryItem {

    public static final String INGOT_CHARGE_LEVEL = "ChargeLevel";

    public ChargedIngotItem(Item replacementItem, int customDataModel, Settings settings) {
        super(replacementItem, customDataModel, GlobalUtils.apply(settings, it -> it.maxCount(1)));
    }

    @Getter @Setter public int maxChargeLevel = 4;
    public int getChargeLevel(ItemStack stack) {
        return GlobalUtils.runOrDefault(stack.getNbt(), 0, it -> it.contains(INGOT_CHARGE_LEVEL) ? it.getInt(INGOT_CHARGE_LEVEL) : 0);
    }

    public void setChargeLevel(ItemStack stack, int chargeLevel) {
        GlobalUtils.apply(stack.getOrCreateNbt(), it -> it.putInt(INGOT_CHARGE_LEVEL, Math.min(chargeLevel, this.maxChargeLevel)));
    }

    public void addChargeLevel(ItemStack stack, int chargeLevel) {
        this.setChargeLevel(stack, this.getChargeLevel(stack) + chargeLevel);
    }

    public boolean isFullyCharged(ItemStack stack) {
        return getChargeLevel(stack) == this.getMaxChargeLevel();
    }

    public static boolean isFullyChargedIngot(ItemStack stack) {
        return stack.getItem() instanceof ChargedIngotItem item && item.isFullyCharged(stack);
    }

    public static boolean isInvalidChargedIngot(ItemStack stack) {
        return stack.getItem() instanceof ChargedIngotItem item && !item.isFullyCharged(stack);
    }

    @Override
    public ItemStack transform(ItemStack sourceStack) {
        return GlobalUtils.create(() -> new ItemStack(replacementItem, sourceStack.getCount()), stack -> {
            if(sourceStack.hasNbt())
                stack.setNbt(sourceStack.getOrCreateNbt().copy());
            stack.getOrCreateNbt().putInt("CustomModelData", this.customDataModel);
            if (!sourceStack.hasNbt() || !sourceStack.getNbt().contains("display")) {
                NBTUtils.getTagOrCompute(stack.getOrCreateNbt(), "display", display -> display.putString("Name", TextUtils.getNonItalicTranslatable("item.minecraft." + Registries.ITEM.getId(sourceStack.getItem()).getPath())));
            }
            if(getChargeLevel(sourceStack) > 0) {
                NBTUtils.computeLore(stack.getOrCreateNbt(), list -> {
                    list.add(NbtString.of(TextUtils.getNonItalicTranslatable("item.minecraft." + Registries.ITEM.getId(sourceStack.getItem()).getPath() + ".charge." + getChargeLevel(sourceStack), text -> text.formatted(Formatting.LIGHT_PURPLE))));
                });
            }
        });
    }
}
