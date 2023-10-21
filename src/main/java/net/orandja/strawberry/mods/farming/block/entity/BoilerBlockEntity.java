package net.orandja.strawberry.mods.farming.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.orandja.strawberry.mods.core.intf.CustomBlockEntity;
import net.orandja.strawberry.mods.core.screen.OverlayedScreenHandler;
import net.orandja.strawberry.mods.farming.Farming;
import net.orandja.strawberry.mods.farming.screen.BoilerScreenHandler;

public class BoilerBlockEntity extends LockableContainerBlockEntity implements CustomBlockEntity {

    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(4, ItemStack.EMPTY);

    public BoilerBlockEntity(BlockPos pos, BlockState state) {
        super(Farming.BOILER_ENTITY, pos, state);
    }


//    @Override
//    public void readNbt(NbtCompound nbt) {
//        super.readNbt(nbt);
//        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
//        Inventories.readNbt(nbt, this.inventory);
//        this.burnTime = nbt.getShort("BurnTime");
//        this.cookTime = nbt.getShort("CookTime");
//        this.cookTimeTotal = nbt.getShort("CookTimeTotal");
//        NbtCompound nbtCompound = nbt.getCompound("RecipesUsed");
//    }
//
//    @Override
//    protected void writeNbt(NbtCompound nbt) {
//        super.writeNbt(nbt);
//        nbt.putShort("BurnTime", (short)this.burnTime);
//        nbt.putShort("CookTime", (short)this.cookTime);
//        nbt.putShort("CookTimeTotal", (short)this.cookTimeTotal);
//        Inventories.writeNbt(nbt, this.inventory);
//        NbtCompound nbtCompound = new NbtCompound();
//        this.recipesUsed.forEach((identifier, count) -> nbtCompound.putInt(identifier.toString(), (int)count));
//        nbt.put("RecipesUsed", nbtCompound);
//    }










    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new BoilerScreenHandler(syncId, playerInventory, this);
    }

    @Override
    protected Text getContainerName() {
        return OverlayedScreenHandler.getName("\uf003").append("\\n test");
    }

    @Override
    public int size() {
        return this.inventory.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemStack : this.inventory) {
            if (itemStack.isEmpty()) continue;
            return false;
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        if (slot >= 0 && slot < this.inventory.size()) {
            return this.inventory.get(slot);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(this.inventory, slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(this.inventory, slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (slot >= 0 && slot < this.inventory.size()) {
            this.inventory.set(slot, stack);
        }
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return Inventory.canPlayerUse(this, player);
    }

    @Override
    public void clear() {
        this.inventory.clear();
    }
}