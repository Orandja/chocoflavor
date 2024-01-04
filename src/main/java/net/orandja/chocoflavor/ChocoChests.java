package net.orandja.chocoflavor;

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
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.orandja.chocoflavor.utils.NBTUtils;
import net.orandja.chocoflavor.utils.PlayerUtils;
import net.orandja.chocoflavor.recipe.WhitelistedChestRecipe;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.stream.Collectors;

public class ChocoChests {
    private ChocoChests() {}
    public static void init() {
        ChocoRecipes.addShapelessRecipe(new Identifier("chocoflavor:whitelisted_chest"), WhitelistedChestRecipe::new);

        ChocoWorlds.ENTITY_CAN_DESTROY.add((world, pos, entity) -> !(world.getBlockEntity(pos) instanceof Handler chest) || chest.canBeDestroyedBy(entity));
        ChocoWorlds.EXPLOSION_CAN_DESTROY.add((world, pos) -> !world.isClient && world.getBlockEntity(pos) instanceof Handler chest && !chest.hasNoWhitelist());
        ChocoWorlds.HOPPER_CANNOT_EXTRACT.add((world, pos) -> (world.getBlockEntity(pos) instanceof Handler chest) && chest.hasWhitelist());
    }

    public static class WhitelistUtils {
        private WhitelistUtils() {}

        public static boolean isStackWhitelisted(ItemStack stack) {
            return stack.hasNbt() && stack.getNbt().contains("whitelist");
        }

        public static boolean compareStack(Handler handler, ItemStack stack) {
            //noinspection ConstantConditions
            if(stack.hasNbt() && stack.getNbt().contains("whitelist")) {
                return stack.getNbt().getList("whitelist", NbtElement.STRING_TYPE).stream().map(NbtElement::asString).filter(handler.getWhitelist()::contains).count() == handler.getWhitelist().size();
            }

            return false;
        }

        public static void notifyPlayer(Handler handler, PlayerEntity player, CallbackInfoReturnable<ActionResult> info) {
            if(!handler.isWhitelisted(player)) {
                String players = handler.getWhitelist().stream().map(PlayerUtils::getUsernameFromUUID).collect(Collectors.joining(","));
                player.sendMessage(Text.literal("Whitelisted for: ").formatted(Formatting.RED, Formatting.BOLD).append(Text.literal("").formatted(Formatting.RESET)).append(Text.literal(players).formatted(Formatting.GREEN)));
                info.setReturnValue(ActionResult.SUCCESS);
            }
        }
    }

    public static class BlockUtils {
        private BlockUtils() {}

        public static void sendBlockUpdate(ServerPlayerEntity player, World world, BlockPos... positions) {
            for (BlockPos position : positions) {
                player.networkHandler.sendPacket(new BlockUpdateS2CPacket(world, position));
            }
        }

        public static BlockPos getAdjascent(BlockState state, BlockPos pos) {
            return switch (state.get(ChestBlock.CHEST_TYPE)) {
                case SINGLE -> null;
                case LEFT -> pos.offset(state.get(ChestBlock.FACING).rotateYClockwise());
                case RIGHT -> pos.offset(state.get(ChestBlock.FACING).rotateYCounterclockwise());
            };
        }

        public static Handler getChest(World world, BlockPos pos) {
            try {
                return !world.isClient ? ((Handler)world.getBlockEntity(pos)) : null;
            } catch(Exception e) {
                return null;
            }
        }
    }

    public interface Handler {

        List<String> getWhitelist();
        default boolean hasWhitelist() {
            return !getWhitelist().isEmpty();
        }
        default boolean hasNoWhitelist() {
            return getWhitelist().isEmpty();
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

        default void loadWhitelist(NbtCompound nbt) {
            NBTUtils.convertToList("whitelist", NbtElement.STRING_TYPE, getWhitelist(), NbtElement::asString, nbt);
        }

        default void saveWhitelist(NbtCompound nbt) {
            NBTUtils.convertToNBTList("whitelist", getWhitelist(), NbtString::of, nbt);
        }

        default void handleStack(ItemStack stack) {
            //noinspection ConstantConditions
            if(stack.hasNbt() && stack.getNbt().contains("whitelist")) {
                stack.getNbt().getList("whitelist", NbtElement.STRING_TYPE).stream().map(NbtElement::asString).forEach(getWhitelist()::add);
            }
        }

        default void onBlockUse(World world, BlockPos pos, PlayerEntity player, CallbackInfoReturnable<ActionResult> info) {
            if(world.getBlockEntity(pos) instanceof Handler chest) {
                WhitelistUtils.notifyPlayer(chest, player, info);
            }
        }

        default void onBlockPlaced(World world, BlockPos pos, ItemStack stack) {
            if(world.getBlockEntity(pos) instanceof Handler chest) {
                chest.handleStack(stack);
            }
        }

        default void onPlacementState(ItemPlacementContext ctx, CallbackInfoReturnable<BlockState> info) {
            if(ctx.getWorld().isClient) {
                return;
            }

            BlockPos adjacentPos = BlockUtils.getAdjascent(info.getReturnValue(), ctx.getBlockPos());
            if(adjacentPos == null) {
                return;
            }
            Handler adjacentChest = BlockUtils.getChest(ctx.getWorld(), adjacentPos);
            if(adjacentChest == null) {
                return;
            }

            if((WhitelistUtils.isStackWhitelisted(ctx.getStack()) && WhitelistUtils.compareStack(adjacentChest, ctx.getStack())) || !adjacentChest.hasWhitelist()) {
                return;
            }

            if(ctx.getPlayer() instanceof ServerPlayerEntity player) {
                BlockUtils.sendBlockUpdate(player, ctx.getWorld(), ctx.getBlockPos(), adjacentPos);
            }
            info.setReturnValue(info.getReturnValue().with(ChestBlock.CHEST_TYPE, ChestType.SINGLE));
        }

    }
}
