package net.orandja.strawberry.blockentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.orandja.chocoflavor.ChocoFlavor;
import net.orandja.chocoflavor.utils.GlobalUtils;
import net.orandja.strawberry.StrawberryCustomBlocks;
import net.orandja.strawberry.intf.StrawberryBlockEntity;
import net.orandja.strawberry.intf.StrawberryMarkerEntity;
import net.orandja.strawberry.intf.StrawberryPlayer;
import net.orandja.strawberry.item.TeleportingEssence;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class TeleporterBlockEntity extends BlockEntity implements StrawberryBlockEntity {

    private final List<TeleportEntry> entries = new ArrayList<>();
    private int index = 0;

    public static class TeleportEntry {
        private final RegistryKey<World> worldKey;
        private final String label;
        private final BlockPos pos;
        private ServerWorld world;

        private static NbtElement encodeWorldKey(RegistryKey<World> key) {
            return World.CODEC.encodeStart(NbtOps.INSTANCE, key).resultOrPartial(ChocoFlavor.LOGGER::error).orElse(null);
        }

        public TeleportEntry(String label, BlockPos pos, RegistryKey<World> worldKey) {
            this.label = label;
            this.pos = pos;
            this.worldKey = worldKey;
        }

        public static TeleportEntry fromNBT(NbtCompound nbt) {
            String label = GlobalUtils.runAs(nbt.get("label"), NbtString.class, NbtString::asString); // getString returns an Empty String. We want a null value if empty.
            BlockPos pos = GlobalUtils.runOrNull(nbt.getCompound("pos"), NbtHelper::toBlockPos);
            RegistryKey<World> worldKey = GlobalUtils.runOrNull(nbt.get("worldKey"), it -> World.CODEC.parse(NbtOps.INSTANCE, it).result().get());
            if(pos != null && worldKey != null) {
                return new TeleportEntry(label, pos, worldKey);
            }

            return null;
        }

        public static TeleportEntry fromStack(ItemStack stack) {
            return GlobalUtils.runOrNull(TeleportingEssence.getLocation(stack.getNbt()), it -> new TeleportEntry(stack.hasCustomName() ? stack.getName().getString() : null, it.getPos(), it.getDimension()));
        }

        public ServerWorld getServerWorld() {
            return this.world == null ? this.world = ChocoFlavor.serverReference.get().getWorld(this.worldKey) : this.world;
        }

        public NbtCompound toNBT() {
            return GlobalUtils.apply(new NbtCompound(), nbt -> {
                if(this.label != null) nbt.putString("label", this.label);
                nbt.put("pos", GlobalUtils.run(this.pos, NbtHelper::fromBlockPos));
                GlobalUtils.run(encodeWorldKey(this.worldKey), it -> nbt.put("worldKey", it));
            });
        }

        public void teleport(PlayerEntity player) {
            if(getServerWorld() != null) {
                world.sendEntityStatus(player, EntityStatuses.ADD_PORTAL_PARTICLES);
                world.emitGameEvent(GameEvent.TELEPORT, player.getPos(), GameEvent.Emitter.of(player));
                world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);

                player.teleport(world, this.pos.getX(), this.pos.getY(), this.pos.getZ(), EnumSet.noneOf(PositionFlag.class), player.getYaw(), player.getPitch());

                world.emitGameEvent(GameEvent.TELEPORT, player.getPos(), GameEvent.Emitter.of(player));
                world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
                player.playSound(SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, 1.0F, 1.0F);
                world.sendEntityStatus(player, EntityStatuses.ADD_PORTAL_PARTICLES);
            }
        }

        public MutableText getText() {
            if(this.label == null) {
                return Text.literal("{"+ this.worldKey.getValue().toString() + ";" + this.pos.getX() + ";" + this.pos.getY() + ";" + this.pos.getZ() + "}");
            }
            return Text.literal(this.label);
        }
    }

    public TeleporterBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(StrawberryCustomBlocks.TELEPORTER_BLOCK_ENTITY_TYPE, blockPos, blockState);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.put("entries", GlobalUtils.apply(new NbtList(), it -> this.entries.forEach(entry -> it.add(entry.toNBT()))));
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        nbt.getList("entries", NbtElement.COMPOUND_TYPE).forEach(it -> GlobalUtils.apply(TeleportEntry.fromNBT((NbtCompound) it), this.entries::add));
    }

    public boolean addLocation(PlayerEntity player, ItemStack stack) {
        if(this.entries.size() < 4) {
            GlobalUtils.apply(TeleportEntry.fromStack(stack), it -> {
                this.entries.add(it);
                player.sendMessage(Text.translatable("teleporter.announce_add", it.getText()).formatted(Formatting.DARK_RED), true);
            });

            this.markDirty();
            return true;
        }
        player.sendMessage(Text.translatable("teleporter.announce_full").formatted(Formatting.DARK_RED), true);
        return false;
    }

    public void changeIndex(PlayerEntity player) {
        if(!this.entries.isEmpty()) {
            this.index = (this.index + 1) % this.entries.size();
        }
        this.announceIndex(player);
    }

    public void announceIndex(PlayerEntity player) {
        if(this.entries.isEmpty()) {
            player.sendMessage(Text.translatable("teleporter.announce_empty").formatted(Formatting.DARK_RED), true);
            return;
        }

        player.sendMessage(Text.translatable("teleporter.announce", GlobalUtils.apply(Text.literal(""), it -> {
            for (int i = 0; i < this.entries.size(); i++) {
                if(i > 0) it.append(Text.literal(", ").formatted(Formatting.WHITE));
                TeleportEntry entry = this.entries.get(i);
                if(this.index == i) it.append(entry.getText().formatted(Formatting.GREEN, Formatting.ITALIC));
                else it.append(entry.getText().formatted(Formatting.GRAY));
            }
        })), true);

    }

    public void tryTeleport(PlayerEntity player) {
        if(!this.entries.isEmpty()) {
            this.entries.get(this.index).teleport(player);
            player.sendMessage(Text.translatable("teleporter.announce_teleporting", this.entries.get(this.index).getText()).formatted(Formatting.WHITE, Formatting.ITALIC), true);
            return;
        }
        player.sendMessage(Text.translatable("teleporter.announce_empty").formatted(Formatting.RED), true);
    }
}
