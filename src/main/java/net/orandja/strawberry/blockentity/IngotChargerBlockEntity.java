package net.orandja.strawberry.blockentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.orandja.chocoflavor.utils.GlobalUtils;
import net.orandja.strawberry.intf.StrawberryBlockEntity;
import net.orandja.strawberry.item.ChargedIngotItem;
import net.orandja.strawberry.screen.IngotChargerContainerScreenHandler;

import static net.orandja.strawberry.StrawberryCustomBlocks.INGOT_CHARGER_ENTITY_TYPE;

public class IngotChargerBlockEntity extends LootableContainerBlockEntity implements StrawberryBlockEntity {
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(9, ItemStack.EMPTY);

    public IngotChargerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(INGOT_CHARGER_ENTITY_TYPE, blockPos, blockState);
    }

    @Override
    protected Text getContainerName() {
        return Text.translatable("container.ingot_charger");
    }

    @Override
    protected DefaultedList<ItemStack> getInvStackList() {
        return this.inventory;
    }

    @Override
    protected void setInvStackList(DefaultedList<ItemStack> list) {
        this.inventory = list;
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new IngotChargerContainerScreenHandler(syncId, playerInventory, this);
    }
    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        if (!this.deserializeLootTable(nbt)) {
            Inventories.readNbt(nbt, this.inventory);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (!this.serializeLootTable(nbt)) {
            Inventories.writeNbt(nbt, this.inventory);
        }
    }

    @Override
    public int size() {
        return 9;
    }

    public void chargeIngots(int chargeLevel) {
        for (ItemStack stack : this.inventory) {
            GlobalUtils.applyAs(stack.getItem(), ChargedIngotItem.class, it -> it.addChargeLevel(stack, chargeLevel));
        }
        this.markDirty();
    }
}
