package net.orandja.chocoflavor;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.orandja.chocoflavor.accessor.LootContextBuilderAccessor;
import net.orandja.chocoflavor.inventory.CloudInventory;
import net.orandja.chocoflavor.recipe.CloudShulkerBoxRecipe;
import net.orandja.chocoflavor.utils.GlobalUtils;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ChocoShulkerBoxes {
    private ChocoShulkerBoxes() {}

    public static void init() {
        ChocoSaveData.onLoad("cloudboxes", it -> {
//            inventories.clear();
            it.getList("boxes", NbtElement.COMPOUND_TYPE).forEach(ChocoShulkerBoxes::addFromStorage);
        });

        ChocoSaveData.onSave("cloudboxes", () -> {
            return GlobalUtils.apply(new NbtCompound(), it -> {
                it.put("boxes", GlobalUtils.apply(new NbtList(), boxes -> {
                    inventories.values().stream().map(inv -> GlobalUtils.apply(new NbtCompound(), inv::toStorage)).forEach(boxes::add);
                }));
            });
        });

        ChocoRecipes.addShapelessRecipe(new Identifier("chocoflavor", "cloudbox"), CloudShulkerBoxRecipe::new);
    }

    public static class Utils {
        public static boolean isShulkerBoxEmpty(ItemStack stack) {
            return !stack.hasNbt() ||
                    !stack.getNbt().contains("BlockEntityTag") ||
                    !stack.getNbt().getCompound("BlockEntityTag").contains("Items") ||
                    stack.getNbt().getCompound("BlockEntityTag").getList("Items", NbtElement.COMPOUND_TYPE).isEmpty();
        }

        public static boolean hasValidChannelName(ItemStack stack) {
            return Block.getBlockFromItem(stack.getItem()) instanceof ShulkerBoxBlock && stack.hasCustomName() && stack.getName().getString().length() > 2 && stack.hasNbt() && !stack.getNbt().contains(CloudInventory.STACK_KEY);
        }

        public static boolean isValidNamedEmptyShulkerBox(ItemStack stack) {
            return hasValidChannelName(stack) && isShulkerBoxEmpty(stack);
        }
    }

    public static final Map<String, CloudInventory> inventories = new HashMap<>();
    public static final DefaultedList<ItemStack> EMPTY = DefaultedList.ofSize(27, ItemStack.EMPTY);
    private static CloudInventory getCloudInventory(String name) {
        return inventories.computeIfAbsent(name, key -> new CloudInventory(name));
    }
    private static void addCloudInventory(CloudInventory inventory) {
        if(!inventories.containsKey(inventory.getName()))
            inventories.put(inventory.getName(), inventory);
    }
    private static void addFromStorage(NbtElement element) {
        GlobalUtils.runAs(element, NbtCompound.class, CloudInventory::loadFromStorage, ChocoShulkerBoxes::addCloudInventory);
    }

    public interface CloudHandler {

        CloudInventory getCloud();
        void setCloud(CloudInventory cloud);
        default void loadCloud(CloudInventory cloud) {
            setCloud(cloud);
            setBoxInventory(cloud.getInventory());
        }
        default boolean hasCloud() {
            return getCloud() != null;
        }

        default void clearCloud() {
            if(hasCloud()) {
                setBoxInventory(EMPTY);
                setName(null);
            }
        }

        void setName(Text text);
        void setBoxInventory(DefaultedList<ItemStack> list);

        default void readBoxTag(NbtCompound tag, CallbackInfo info, Consumer<NbtCompound> superRead) {
            if(tag.contains(CloudInventory.STACK_KEY)) {
                superRead.accept(tag);
                loadCloud(getCloudInventory(tag.getString(CloudInventory.STACK_KEY)));
                info.cancel();
            }
        }

        default void writeBoxTag(NbtCompound tag, CallbackInfo info, Consumer<NbtCompound> superWrite) {
            if(hasCloud()) {
                superWrite.accept(tag);
                getCloud().toEntityStorage(tag);
                info.cancel();
            }
        }
    }

    public interface BlockHandler {
        default void clearCloud(World world, BlockPos pos) {
            if(world.getBlockEntity(pos) instanceof CloudHandler handler) {
                handler.clearCloud();
            }
        }

        default void channelCloud(World world, BlockPos pos, ItemStack stack) {
            if(stack.hasNbt() && world.getBlockEntity(pos) instanceof CloudHandler handler) {
                GlobalUtils.applyAs(stack.getOrCreateNbt().get(CloudInventory.STACK_KEY), NbtString.class, channel -> {
                    handler.loadCloud(getCloudInventory(channel.asString()));
                });
            }
        }

        default void lootCloud(GlobalUtils.SupplierBiParameters<List<ItemStack>, BlockState, LootContextParameterSet.Builder> getStacks, BlockState state, LootContextParameterSet.Builder builder, CallbackInfoReturnable<List<ItemStack>> info) {
            Object blockEntity = GlobalUtils.run(((LootContextBuilderAccessor)builder).getParameters(), it -> it.get(LootContextParameters.BLOCK_ENTITY));
            Object lootingEntity = GlobalUtils.run(((LootContextBuilderAccessor)builder).getParameters(), it -> it.get(LootContextParameters.THIS_ENTITY));
            if(blockEntity instanceof CloudHandler handler && handler.hasCloud()) {
                if(handler.getCloud().isPublic() || (GlobalUtils.runAsWithDefault(lootingEntity, Entity.class, false, handler.getCloud()::isOwned))) {
                    handler.clearCloud();
                    info.setReturnValue(GlobalUtils.apply(getStacks.create(state, builder), loots -> {
                        loots.forEach(handler.getCloud()::toStack);
                    }));
                }
            }
        }
    }
}
