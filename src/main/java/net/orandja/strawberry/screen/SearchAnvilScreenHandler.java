package net.orandja.strawberry.screen;

import net.minecraft.SharedConstants;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.slot.ForgingSlotsManager;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.orandja.chocoflavor.accessor.AnvilScreenHandlerAccessor;
import net.orandja.chocoflavor.accessor.ScreenHandlerAccessor;
import net.orandja.chocoflavor.utils.GlobalUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * @see {@link net.minecraft.screen.AnvilScreenHandler}
 * */

public class SearchAnvilScreenHandler extends AnvilScreenHandler {

    private final BiConsumer<PlayerEntity, String> onSearch;
    private ItemStack confirmStack;
    private ItemStack toNameStack;
    private Slot outputSlot;
    private Slot toNameSlot;
    private List<ScreenHandlerListener> listeners;
    private String searchTerm = "";

    public SearchAnvilScreenHandler(int syncId, PlayerInventory inventory, String baseTerm, BiConsumer<PlayerEntity, String> onSearch) {
        super(syncId, inventory);
        this.listeners = ((ScreenHandlerAccessor)this).getListeners();
        this.onSearch = onSearch;
        this.toNameStack.setCustomName(Text.literal(baseTerm == null ? "" : baseTerm));
        if(this.player instanceof ServerPlayerEntity player) {
            player.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(this.syncId, 0, toNameSlot.getIndex(), this.toNameStack));
        }
        if(this instanceof AnvilScreenHandlerAccessor accessor) {
            accessor.getLevelCost().set(0);
        }

        if(this.player instanceof ServerPlayerEntity player) {
            player.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(ScreenHandlerSlotUpdateS2CPacket.UPDATE_PLAYER_INVENTORY_SYNC_ID, 0, PlayerInventory.OFF_HAND_SLOT, ItemStack.EMPTY));
        }
    }

    @Override
    public void sendContentUpdates() {
        for (ScreenHandlerListener screenHandlerListener : this.listeners) {
            for (int i = 0; i < this.slots.size(); ++i) {
                screenHandlerListener.onSlotUpdate(this, this.slots.get(i).getIndex(), ItemStack.EMPTY);
            }
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    protected Slot addSlot(Slot slot) {
        if((slot.getIndex() == 0) && slot.inventory != player.getInventory()) {
            return toNameSlot = super.addSlot(new UninteractableSlot(slot.inventory, slot.getIndex(), slot.x, slot.y, GlobalUtils.apply(new ItemStack(Items.BARRIER), it -> {
                it.getOrCreateNbt().putInt("CustomModelData", 1);
                it.setCustomName(Text.literal(""));
                this.toNameStack = it;
            })));
        }
        if((slot.getIndex() == 2) && slot.inventory != player.getInventory()) {
            return outputSlot = super.addSlot(new UninteractableSlot(slot.inventory, slot.getIndex(), slot.x, slot.y, ItemStack.EMPTY));
        }

        if(slot.inventory == player.getInventory() && slot.getIndex() == 8) {
            return super.addSlot(new InteractableSlot(slot.inventory, slot.getIndex(), slot.x, slot.y, GlobalUtils.apply(new ItemStack(Items.BARRIER), it -> {
                it.getOrCreateNbt().putInt("CustomModelData", 2);
                it.setCustomName(Text.literal("Search"));
                this.confirmStack = it;
            }), ignored -> {
                this.onSearch.accept(this.player, searchTerm);
            }));
        }

        return super.addSlot(new UninteractableSlot(slot.inventory, slot.getIndex(), slot.x, slot.y, ItemStack.EMPTY));
    }

    @Override
    protected ForgingSlotsManager getForgingSlotsManager() {
        return ForgingSlotsManager.create().input(0, 27, 47, stack -> false).input(1, 76, 47, stack -> false).output(2, 134, 47).build();
    }

    @Override
    protected boolean canTakeOutput(PlayerEntity player, boolean present) {
        return true;
    }

    @Override
    protected void onTakeOutput(PlayerEntity player, ItemStack stack) {

    }

    @Override
    public void updateResult() {
        if(this instanceof AnvilScreenHandlerAccessor accessor) {
            accessor.getLevelCost().set(0);
        }

        if(this.player instanceof ServerPlayerEntity player) {
            player.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(this.syncId, 0, outputSlot.getIndex(), ItemStack.EMPTY));
        }
    }

    @Override
    public boolean setNewItemName(String newItemName) {
        this.searchTerm = sanitize(newItemName);
        if(this.player instanceof ServerPlayerEntity player) {
            player.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(this.syncId, 0, outputSlot.getIndex(), ItemStack.EMPTY));
        }
        if(this instanceof AnvilScreenHandlerAccessor accessor) {
            accessor.getLevelCost().set(0);
        }
        return true;
    }

    @Nullable
    private static String sanitize(String name) {
        String string = SharedConstants.stripInvalidChars(name);
        if (string.length() <= 50) {
            return string;
        }
        return "";
    }

    public int getLevelCost() {
        return 0;
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        if(this instanceof AnvilScreenHandlerAccessor accessor) {
            accessor.getLevelCost().set(0);
        }
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        if(player instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(ScreenHandlerSlotUpdateS2CPacket.UPDATE_PLAYER_INVENTORY_SYNC_ID, 0, PlayerInventory.OFF_HAND_SLOT, player.getStackInHand(Hand.OFF_HAND)));
        }
    }

    public static final NamedScreenHandlerFactory create(MutableText displayName, String baseTerm, BiConsumer<PlayerEntity, String> onSearch) {
        return new NamedScreenHandlerFactory() {
            @Nullable
            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                return new SearchAnvilScreenHandler(syncId, playerInventory, baseTerm, onSearch);
            }

            @Override
            public Text getDisplayName() {
                return displayName;
            }
        };
    }
}
