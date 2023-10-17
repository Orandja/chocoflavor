package net.orandja.strawberry.mods.core.screen;

import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;

public class RedirectOutputSlot extends RedirectSlot {
    private final PlayerEntity player;
    public int amount;

    public RedirectOutputSlot(Inventory inventory, PlayerEntity player, int fakeIndex, int realIndex) {
        super(inventory, fakeIndex, realIndex);
        this.player = player;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return false;
    }

    @Override
    public ItemStack takeStack(int amount) {
        if (this.hasStack()) {
            this.amount += Math.min(amount, this.getStack().getCount());
        }
        return super.takeStack(amount);
    }

    @Override
    public void onTakeItem(PlayerEntity player, ItemStack stack) {
        this.onCrafted(stack);
        super.onTakeItem(player, stack);
    }

    @Override
    protected void onCrafted(ItemStack stack, int amount) {
        this.amount += amount;
        this.onCrafted(stack);
    }

    @Override
    protected void onCrafted(ItemStack stack) {
        stack.onCraft(this.player.getWorld(), this.player, this.amount);
        Object object = this.player;
        if (object instanceof ServerPlayerEntity serverPlayer) {
            this.onAmount(serverPlayer);
        }
        this.amount = 0;
    }

    public void onAmount(ServerPlayerEntity serverPlayer) {

    }
}

