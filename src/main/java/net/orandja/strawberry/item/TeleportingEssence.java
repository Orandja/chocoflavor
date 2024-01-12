package net.orandja.strawberry.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import net.orandja.chocoflavor.ChocoFlavor;
import net.orandja.chocoflavor.utils.GlobalUtils;
import net.orandja.chocoflavor.utils.NBTUtils;
import net.orandja.strawberry.StrawberryCustomBlocks;
import net.orandja.strawberry.blockentity.TeleporterBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Optional;

public class TeleportingEssence extends StrawberryItem {
    public static final String TELEPORTING_ESSENCE_POS_KEY = "TeleportingEssencePos";
    public static final String TELEPORTING_ESSENCE_DIMENSION_KEY = "TeleportingEssenceDimension";

    public TeleportingEssence(Item replacementItem, int customDataModel, Settings settings) {
        super(replacementItem, customDataModel, GlobalUtils.apply(settings, it -> it.maxCount(1)));
    }

    private void writeNbt(RegistryKey<World> worldKey, BlockPos pos, NbtCompound nbt) {
        nbt.put(TELEPORTING_ESSENCE_POS_KEY, NbtHelper.fromBlockPos(pos));
        nbt.put("Enchantments", GlobalUtils.apply(new NbtList(), it -> it.add(new NbtCompound())));
        NBTUtils.computeLore(nbt, lore -> {
            lore.add(NbtString.of(Text.Serializer.toJson(Text.literal("Point: {"+ worldKey.getValue().toString() + ";" + pos.getX() + ";" + pos.getY() + ";" + pos.getZ() + "}").formatted(Formatting.LIGHT_PURPLE))));
        });
        World.CODEC.encodeStart(NbtOps.INSTANCE, worldKey).resultOrPartial(ChocoFlavor.LOGGER::error).ifPresent(nbtElement -> nbt.put(TELEPORTING_ESSENCE_DIMENSION_KEY, nbtElement));
    }

    public static boolean hasLocation(ItemStack stack) {
        return GlobalUtils.runOrDefault( stack.getNbt(), false, it -> it.contains(TELEPORTING_ESSENCE_DIMENSION_KEY) || it.contains(TELEPORTING_ESSENCE_POS_KEY));
    }

    private static Optional<RegistryKey<World>> getLocationDimension(NbtCompound nbt) {
        return World.CODEC.parse(NbtOps.INSTANCE, nbt.get(TELEPORTING_ESSENCE_DIMENSION_KEY)).result();
    }

    @Nullable
    public static GlobalPos getLocation(NbtCompound nbt) {
        Optional<RegistryKey<World>> optional;
        if (nbt != null && nbt.contains(TELEPORTING_ESSENCE_POS_KEY) && nbt.contains(TELEPORTING_ESSENCE_DIMENSION_KEY) && (optional = getLocationDimension(nbt)).isPresent()) {
            return GlobalPos.create(optional.get(), NbtHelper.toBlockPos(nbt.getCompound(TELEPORTING_ESSENCE_POS_KEY)));
        }
        return null;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (world.isClient) {
            return TypedActionResult.success(itemStack);
        }

        if(hasLocation(itemStack)) {
            GlobalUtils.apply(getLocation(itemStack.getNbt()), it -> user.teleport(ChocoFlavor.serverReference.get().getWorld(it.getDimension()), it.getPos().getX(), it.getPos().getY(), it.getPos().getZ(), EnumSet.noneOf(PositionFlag.class), user.getYaw(), user.getPitch()));
            if(!user.isCreative())
                itemStack.decrement(1);
            return TypedActionResult.consume(itemStack);
        } else {
            this.writeNbt(world.getRegistryKey(), user.getBlockPos(), itemStack.getOrCreateNbt());
            return TypedActionResult.fail(itemStack);
        }
    }
}
