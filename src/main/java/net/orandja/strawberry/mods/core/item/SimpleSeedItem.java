package net.orandja.strawberry.mods.core.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.orandja.chocoflavor.utils.Utils;
import net.orandja.strawberry.mods.core.TripWireBlockData;
import net.orandja.strawberry.mods.core.intf.StrawberryItem;

public class SimpleSeedItem extends AliasedBlockItem implements StrawberryItem {

    private final int customDataModel;
    private final Item replacementItem;
    private final String texture;
    private final TripWireBlockData tripwireblockData;

    public SimpleSeedItem(Block block, String texture, int customDataModel, Settings settings) {
        super(block, settings);
        this.texture = texture;
        this.customDataModel = customDataModel;
        this.replacementItem = Items.WHEAT_SEEDS;
        this.tripwireblockData = TripWireBlockData.fromID(this.customDataModel);
    }

    public SimpleSeedItem(Block block, int customDataModel, Settings settings) {
        this(block, null, customDataModel, settings);
    }

    protected boolean place(ItemPlacementContext context, BlockState state) {
        return context.getWorld().setBlockState(context.getBlockPos(), state, 18);
    }

    @Override
    public ItemStack transform(ItemStack sourceStack) {
        return Utils.apply(transform(sourceStack, this.replacementItem, this.customDataModel), it -> {
            it.getOrCreateNbt().put("BlockStateTag", Utils.create(NbtCompound::new, tag -> {
                tag.putString("attached", tripwireblockData.attached ? "true" : "false");
                tag.putString("powered", tripwireblockData.powered ? "true" : "false");
                tag.putString("east", tripwireblockData.east ? "true" : "false");
                tag.putString("north", tripwireblockData.north ? "true" : "false");
                tag.putString("south", tripwireblockData.south ? "true" : "false");
                tag.putString("west", tripwireblockData.west ? "true" : "false");
                tag.putString("disarmed", tripwireblockData.disarmed ? "true" : "false");
            }));
        });
    }

    @Override
    public void register() {
        register(this.replacementItem, this.customDataModel, Registries.ITEM.getId(this).getPath(), texture == null ? Registries.ITEM.getId(this).getPath() : this.texture);
    }

    @Override
    public ActionResult place(ItemPlacementContext context) {
        return Utils.apply(super.place(context), result -> {
          if(!result.isAccepted() && context.getPlayer() instanceof  ServerPlayerEntity player) {
              player.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(-2, 0, context.getHand() == Hand.MAIN_HAND ? player.getInventory().selectedSlot : PlayerInventory.OFF_HAND_SLOT, player.getStackInHand(context.getHand())));
          }
        });
    }
}
