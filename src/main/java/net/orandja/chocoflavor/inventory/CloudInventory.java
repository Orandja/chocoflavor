package net.orandja.chocoflavor.inventory;

import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import net.orandja.chocoflavor.ChocoShulkerBoxes;
import net.orandja.chocoflavor.utils.GlobalUtils;
import net.orandja.chocoflavor.utils.StackUtils;

public class CloudInventory {

    public static final String STORAGE_KEY = "channel";
    public static final String STACK_KEY = "vw_channel";

    @Getter private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(27, ItemStack.EMPTY);
    @Getter private final String name;

    public static CloudInventory loadFromStorage(NbtCompound nbt) {
//        return GlobalUtils.apply(GlobalUtils.runAs(nbt.get(STORAGE_KEY), NbtString.class, CloudInventory::new), it -> it.fromStorage(nbt));
        return GlobalUtils.runAs(nbt.get(STORAGE_KEY), NbtString.class, CloudInventory::new, it -> it.fromStorage(nbt));
    }

    public CloudInventory(NbtCompound tag) {
        this(tag.getString(STACK_KEY));
    }

    public CloudInventory(NbtString name) {
        this(name.asString());
    }
    public CloudInventory(String name) {
        this.name = name;
    }

    public boolean isPublic() {
        return name.startsWith("public:");
    }

    public boolean isOwned(Entity entity) {
        if(!isPublic() && entity != null) {
            if(PlayerEntity.class.isAssignableFrom(entity.getClass())) {
                return name.startsWith(entity.getUuidAsString() + ":");
            }

            if(TntEntity.class.isAssignableFrom(entity.getClass())) {
                return isOwned(((TntEntity)entity).getOwner());
            }
        }
        return false;
    }

    public NbtCompound toStorage(NbtCompound nbt) {
        nbt.putString(STORAGE_KEY, name);
        Inventories.writeNbt(nbt, inventory, false);
        return nbt;
    }

    public void fromStorage(NbtCompound nbt) {
        if(nbt.contains("Items", NbtElement.LIST_TYPE)) {
            Inventories.readNbt(nbt, inventory);
        }
    }

    public NbtCompound toEntityStorage(NbtCompound nbt) {
        nbt.putString(STACK_KEY, name);
        Inventories.writeNbt(nbt, ChocoShulkerBoxes.EMPTY, false);
        return nbt;
    }

    public String getTextual() {
        return GlobalUtils.run(this.name.split(":"), it -> it[1] + " of " + it[0]);
    }

    public ItemStack toStack(ItemStack stack) {
        GlobalUtils.apply(stack.getOrCreateNbt(), it -> {
            it.putString(STACK_KEY, name);
        });

        StackUtils.computeLore(stack, lore -> {
            lore.clear();
            lore.add(NbtString.of(Text.Serializer.toJson(Text.literal(getTextual()).formatted(Formatting.BLUE))));
        });

        stack.setCustomName(Text.literal("[Cloud Box]").formatted(Formatting.GREEN));
        return stack;
    }
}
