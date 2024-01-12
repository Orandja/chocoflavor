package net.orandja.strawberry.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.orandja.chocoflavor.utils.GlobalUtils;
import net.orandja.strawberry.blockdata.TripWireBlockData;
import net.orandja.strawberry.intf.StrawberryItemHandler;

public class StrawberrySeedItem extends AliasedBlockItem implements StrawberryItemHandler {

    private final int customDataModel;
    private final Item replacementItem;
    private final String texture;
    private final TripWireBlockData tripwireblockData;

    public StrawberrySeedItem(Block block, String texture, int customDataModel, Settings settings) {
        super(block, settings);
        this.texture = texture;
        this.customDataModel = customDataModel;
        this.replacementItem = Items.WHEAT_SEEDS;
        this.tripwireblockData = TripWireBlockData.fromID(this.customDataModel);
    }

    public StrawberrySeedItem(Block block, int customDataModel, Settings settings) {
        this(block, null, customDataModel, settings);
    }

    protected boolean place(ItemPlacementContext context, BlockState state) {
        return context.getWorld().setBlockState(context.getBlockPos(), state, 18);
    }

    @Override
    public ItemStack transform(ItemStack sourceStack) {
        return GlobalUtils.apply(transform(sourceStack, this.replacementItem, this.customDataModel), it -> {
            it.getOrCreateNbt().put("BlockStateTag", GlobalUtils.create(NbtCompound::new, tag -> {
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
    public ActionResult place(ItemPlacementContext context) {
        return GlobalUtils.apply(super.place(context), result -> {
          if(!result.isAccepted() && context.getPlayer() instanceof  ServerPlayerEntity player) {
              player.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(-2, 0, context.getHand() == Hand.MAIN_HAND ? player.getInventory().selectedSlot : PlayerInventory.OFF_HAND_SLOT, player.getStackInHand(context.getHand())));
          }
        });
    }
}
