package net.orandja.chocoflavor.mods.whitelistedchestblock;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.enums.ChestType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.orandja.chocoflavor.mods.core.ProtectBlock;
import net.orandja.chocoflavor.mods.core.CustomRecipe;
import net.orandja.chocoflavor.utils.NBTUtils;
import net.orandja.chocoflavor.utils.PlayerUtils;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.stream.Collectors;

public interface WhitelistedChestBlock {

    static boolean stackHasWhitelist(ItemStack stack) {
        //noinspection ConstantConditions
        return stack.hasNbt() && stack.getNbt().contains("whitelist");
    }

    static void sendBlockUpdate(ServerPlayerEntity player, World world, BlockPos... pos) {
        for (BlockPos p : pos) {
            player.networkHandler.sendPacket(new BlockUpdateS2CPacket(world, p));
        }
    }

    static BlockPos getAdjascentPos(BlockState state, BlockPos pos) {
        return switch (state.get(ChestBlock.CHEST_TYPE)) {
            case SINGLE -> null;
            case LEFT -> pos.offset(state.get(ChestBlock.FACING).rotateYClockwise());
            case RIGHT -> pos.offset(state.get(ChestBlock.FACING).rotateYCounterclockwise());
        };
    }

    static WhitelistedChestBlock getChest(World world, BlockPos pos) {
        try {
            return !world.isClient ? ((WhitelistedChestBlock)world.getBlockEntity(pos)) : null;
        } catch(Exception e) {
            return null;
        }
    }

    static void beforeLaunch() {
        CustomRecipe.customShapelessRecipes.put(new Identifier("chocoflavor:whitelisted_chest"), WhitelistedChestRecipe::new);

        ProtectBlock.ENTITY_CAN_DESTROY.add((world, pos, entity) -> !(world.getBlockEntity(pos) instanceof WhitelistedChestBlock chest) || chest.canBeDestroyedBy(entity));
        ProtectBlock.EXPLOSION_CAN_DESTROY.add((world, pos) -> !world.isClient && world.getBlockEntity(pos) instanceof WhitelistedChestBlock chest && !chest.hasNoWhitelist());
        ProtectBlock.HOPPER_CANNOT_EXTRACT.add((world, pos) -> (world.getBlockEntity(pos) instanceof WhitelistedChestBlock chest) && chest.hasWhitelist());
    }

    List<String> getWhitelist();

    default void loadWhitelist(NbtCompound nbt) {
        NBTUtils.convertToList("whitelist", NbtElement.STRING_TYPE, getWhitelist(), NbtElement::asString, nbt);
    }

    default void saveWhitelist(NbtCompound nbt) {
        NBTUtils.convertToNBTList("whitelist", getWhitelist(), NbtString::of, nbt);
    }

    default boolean hasWhitelist() {
        return getWhitelist().size() > 0;
    }

    default boolean hasNoWhitelist() {
        return getWhitelist().size() == 0;
    }

    default boolean canBeDestroyedBy(Entity entity) {
        return hasNoWhitelist() || isWhitelisted(entity);
    }

    default boolean isWhitelisted(Entity entity) {
        if(entity instanceof PlayerEntity player) {
            return hasNoWhitelist() || player.isCreative() || getWhitelist().contains(player.getUuidAsString());
        } else {
            return false;
        }
    }

    default boolean compareStack(ItemStack stack) {
        //noinspection ConstantConditions
        if(stack.hasNbt() && stack.getNbt().contains("whitelist")) {
            return stack.getNbt().getList("whitelist", NbtElement.STRING_TYPE).stream().map(NbtElement::asString).filter(getWhitelist()::contains).count() == getWhitelist().size();

        }

        return false;
    }

    default void notifyPlayer(PlayerEntity player, CallbackInfoReturnable<ActionResult> info) {
        if(!isWhitelisted(player)) {
            String players = getWhitelist().stream().map(PlayerUtils::getUsernameFromUUID).collect(Collectors.joining(","));
            player.sendMessage(Text.of("[{\"text\":\"Whitelisted for: \",\"color\":\"red\"},{\"text\":\""+ players +"\",\"color\":\"green\"}]"), true);
            info.setReturnValue(ActionResult.SUCCESS);
        }
    }

    default void handleStack(ItemStack stack) {
        //noinspection ConstantConditions
        if(stack.hasNbt() && stack.getNbt().contains("whitelist")) {
            stack.getNbt().getList("whitelist", NbtElement.STRING_TYPE).stream().map(NbtElement::asString).forEach(getWhitelist()::add);
        }
    }

    default void onBlockUse(World world, BlockPos pos, PlayerEntity player, CallbackInfoReturnable<ActionResult> info) {
        if(world.getBlockEntity(pos) instanceof WhitelistedChestBlock chest) {
            chest.notifyPlayer(player, info);
        }
    }

    default void onBlockPlaced(World world, BlockPos pos, ItemStack stack) {
        if(world.getBlockEntity(pos) instanceof WhitelistedChestBlock chest) {
            chest.handleStack(stack);
        }
    }

    default void onPlacementState(ItemPlacementContext ctx, CallbackInfoReturnable<BlockState> info) {
        if(ctx.getWorld().isClient) {
            return;
        }

        BlockPos adjacentPos = getAdjascentPos(info.getReturnValue(), ctx.getBlockPos());
        if(adjacentPos == null) {
            return;
        }
        WhitelistedChestBlock adjacentChest = getChest(ctx.getWorld(), adjacentPos);
        if(adjacentChest == null) {
            return;
        }

        if((stackHasWhitelist(ctx.getStack()) && adjacentChest.compareStack(ctx.getStack())) || !adjacentChest.hasWhitelist()) {
            return;
        }

        if(ctx.getPlayer() instanceof ServerPlayerEntity player) {
            sendBlockUpdate(player, ctx.getWorld(), ctx.getBlockPos(), adjacentPos);
        }
        info.setReturnValue(info.getReturnValue().with(ChestBlock.CHEST_TYPE, ChestType.SINGLE));
    }
}
