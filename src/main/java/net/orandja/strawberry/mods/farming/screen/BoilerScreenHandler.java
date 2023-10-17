package net.orandja.strawberry.mods.farming.screen;

import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.FurnaceFuelSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.orandja.chocoflavor.ChocoFlavor;
import net.orandja.chocoflavor.utils.MathUtils;
import net.orandja.strawberry.mods.core.intf.ItemStackTransformer;
import net.orandja.strawberry.mods.core.screen.OverlayedScreenHandler;
import net.orandja.strawberry.mods.core.screen.RedirectOutputSlot;
import net.orandja.strawberry.mods.core.screen.RedirectSlot;

public class BoilerScreenHandler extends OverlayedScreenHandler {

    public static final ItemStack PROGRESS_0_21 = ItemStackTransformer.createCustomNamedStack(new ItemStack(Items.BARRIER), 10021, "");
    public static final ItemStack PROGRESS_15_21 = ItemStackTransformer.createCustomNamedStack(new ItemStack(Items.BARRIER), 11521, "");

    public final PlayerEntity player;

    public BoilerScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(ScreenHandlerType.GENERIC_9X6, syncId, playerInventory, inventory, 6,
                new RedirectSlot(inventory, 11, 0),
                new RedirectSlot(inventory, 20, 1) {
                    @Override
                    public boolean canInsert(ItemStack stack) {
                        return stack.isOf(Items.WATER_BUCKET) || (stack.isOf(Items.POTION) && PotionUtil.getPotion(stack).equals(Potions.WATER));
                    }
                },
                new RedirectSlot(inventory, 29, 2) {
                    @Override
                    public boolean canInsert(ItemStack stack) {
                        return AbstractFurnaceBlockEntity.canUseAsFuel(stack) || isBucket(stack);
                    }

                    @Override
                    public int getMaxItemCount(ItemStack stack) {
                        return isBucket(stack) ? 1 : super.getMaxItemCount(stack);
                    }

                    public static boolean isBucket(ItemStack stack) {
                        return stack.isOf(Items.BUCKET);
                    }
                },
                new RedirectOutputSlot(inventory, playerInventory.player, 24, 3));

        this.player = playerInventory.player;
    }

    @Override
    public void updateToClient() {
        super.updateToClient();
        if(this.player instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(syncId, nextRevision(), 23, PROGRESS_15_21));
        }
    }
}
