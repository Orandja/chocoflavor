package net.orandja.chocoflavor.mods.cloudshulkerbox;

import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.orandja.chocoflavor.mods.cloudshulkerbox.accessor.LootContextBuilderAccessor;
import net.orandja.chocoflavor.mods.core.CustomRecipe;
import net.orandja.chocoflavor.mods.core.ExtraSaveData;
import net.orandja.chocoflavor.utils.InventoryUtils;
import net.orandja.chocoflavor.utils.NBTUtils;
import net.orandja.chocoflavor.utils.StackUtils;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public interface CloudShulkerBox {

    static boolean isShulkerBoxEmpty(ItemStack stack) {
        return !stack.hasNbt() || !stack.getNbt().contains("BlockEntityTag") || !stack.getNbt().getCompound("BlockEntityTag").contains("Items") || stack.getNbt().getCompound("BlockEntityTag").getList("Items", NbtElement.COMPOUND_TYPE).size() == 0;
    }

    static boolean hasValidChannelName(ItemStack stack) {
        return Block.getBlockFromItem(stack.getItem()) instanceof ShulkerBoxBlock && stack.hasCustomName() && stack.getName().getString().length() > 2 && stack.hasNbt() && !stack.getNbt().contains("vw_channel");
    }

    static boolean isValidNamedEmptyShulkerBox(ItemStack stack) {
        return hasValidChannelName(stack) && isShulkerBoxEmpty(stack);
    }

    class CloudShulkerBoxRecipe extends ShapelessRecipe {

        public CloudShulkerBoxRecipe(String group, CraftingRecipeCategory category, ItemStack itemStack, DefaultedList<Ingredient> defaultedList) {
            super(group, category, itemStack, defaultedList);
        }

        @Override
        public boolean isIgnoredInRecipeBook() {
            return super.isIgnoredInRecipeBook();
        }

        @Override
        public ItemStack getResult(DynamicRegistryManager dynamicRegistryManager) {
            return ItemStack.EMPTY;
        }

        @Override
        public boolean matches(RecipeInputInventory craftingInventory, World world) {
            return super.matches(craftingInventory, world) && InventoryUtils.toStream(craftingInventory).anyMatch(CloudShulkerBox::isValidNamedEmptyShulkerBox);
        }

        @Override
        public ItemStack craft(RecipeInputInventory craftingInventory, DynamicRegistryManager dynamicRegistryManager) {
            Optional<ItemStack> optionalOutput = InventoryUtils.toStream(craftingInventory).filter(CloudShulkerBox::hasValidChannelName).findFirst();
            if(optionalOutput.isEmpty()) {
                return ItemStack.EMPTY;
            }

            ItemStack output = optionalOutput.get().copy();

            String channel = output.getName().getString();
            String channelLiteral;
            if(channel.startsWith(":")) {
                PlayerEntity player = InventoryUtils.getPlayer(craftingInventory);
                channel = channel.substring(1);
                channelLiteral = channel + " of " + player.getEntityName();
                channel = player.getUuidAsString() + ":" + channel;
            } else {
                channelLiteral = channel + " of public";
                channel = "public:" + channel;
            }

            NbtCompound tag = output.getOrCreateNbt();
            tag.putString("vw_channel", channel);
            tag.putString("vw_channel_literal", channelLiteral);
            NBTUtils.getOrCreate(tag, "Enchantments", key -> tag.getList(key, NbtElement.COMPOUND_TYPE), NbtList::new);
            StackUtils.computeLore(output, lore -> {
                lore.add(NbtString.of("{\"text\":\""+ channelLiteral +"\", \"color\":\"blue\"}"));
            });

            return output;
        }
    }

    class CloudChannel {
        private final String literal;
        private final String name;

        public static Pair<String, String> getChannel(NbtCompound tag) {
            if(tag.contains("vw_channel")) {
                return new Pair<>(tag.getString("vw_channel"), tag.getString("vw_channel_literal"));
            }

            if(tag.contains("BlockEntityTag") && tag.getCompound("BlockEntityTag").contains("vw_channel")) {
                NbtCompound entityTag = tag.getCompound("BlockEntityTag");
                return new Pair<>(entityTag.getString("vw_channel"), entityTag.getString("vw_channel_literal"));
            }
            return null;
        }

        public static Pair<String, String> getChannel(ItemStack stack) {
            if(stack.hasNbt()) {
                return getChannel(stack.getNbt());
            }
            return null;
        }

        public CloudChannel(String name, String literal) {
            this.name = name;
            this.literal = literal;
        }

        public CloudChannel(NbtCompound tag) {
            this(tag.getString("vw_channel"), tag.getString("vw_channel_literal"));
        }

        public boolean isPublic() {
            return name.startsWith("public:");
        }

        public boolean isOwned(Entity entity) {
            if(!isPublic() && entity != null) {
                if(PlayerEntity.class.isAssignableFrom(entity.getClass())) {
                    return name.startsWith(entity.getUuidAsString() + ":");
                }

                if(TntEntity.class.isAssignableFrom(entity.getClass())) {
                    return isOwned(((TntEntity)entity).getOwner());
                }
            }
            return false;
        }

        public void writeToNbt(NbtCompound tag) {
            tag.putString("vw_channel", name);
            tag.putString("vw_channel_literal", literal);
        }

        public void writeToStack(ItemStack stack) {
            StackUtils.computeTag(stack, this::writeToNbt);

            StackUtils.computeLore(stack, lore -> {
                lore.clear();
                lore.add(NbtString.of("{\"text\":\""+ literal +"\", \"color\":\"blue\"}"));
            });

            stack.setCustomName(Text.Serializer.fromJson("{\"text\":\"[Cloud Box] \",\"color\":\"green\"}"));
        }

        public String getLiteral() {
            return literal;
        }

        public String getName() {
            return name;
        }
    }

    DefaultedList<ItemStack> EMPTY = DefaultedList.ofSize(27, ItemStack.EMPTY);
    Map<String, DefaultedList<ItemStack>> CLOUDBOXES = Maps.newHashMap();

    static void beforeLaunch() {
        ExtraSaveData.onLoad("cloudboxes", it -> {
            CLOUDBOXES.clear();
            it.getList("boxes", NbtElement.COMPOUND_TYPE).forEach(element -> {
                if(element instanceof NbtCompound cloudbox) {
                    String channel = cloudbox.getString("channel");
                    getCloudBox(cloudbox, inventory -> {
                        if(cloudbox.contains("Items", NbtElement.LIST_TYPE)) {
                            Inventories.readNbt(cloudbox, inventory);
                        }
                    });
                }
            });
        });

        ExtraSaveData.onSave("cloudboxes", () -> {
            NbtCompound compound = new NbtCompound();

            NbtList boxes = new NbtList();
            compound.put("boxes", boxes);
            CLOUDBOXES.forEach((name, contents) -> {
                NbtCompound box = new NbtCompound();
                box.putString("channel", name);
                Inventories.writeNbt(box, contents, false);
                boxes.add(box);
            });

            return compound;
        });

        CustomRecipe.customShapelessRecipes.put(new Identifier("chocoflavor", "cloudbox"), CloudShulkerBoxRecipe::new);
    }

    static void getCloudBox(String channel, Consumer<DefaultedList<ItemStack>> consumer) {
        consumer.accept(getCloudBox(channel));
    }

    static void getCloudBox(NbtCompound tag, Consumer<DefaultedList<ItemStack>> consumer) {
        getCloudBox(tag.getString("channel"), consumer);
    }

    static DefaultedList<ItemStack> getCloudBox(String channel) {
        return CLOUDBOXES.computeIfAbsent(channel, key -> DefaultedList.ofSize(27, ItemStack.EMPTY));
    }

    static DefaultedList<ItemStack> getCloudBox(CloudChannel channel) {
        return getCloudBox(channel.getName());
    }

    CloudChannel getChannel();
    void setChannel(CloudChannel channel);

    default boolean hasChannel() {
        return this.getChannel() != null;
    }

    default void setChannel(String name, String literal) {
        this.setChannel(new CloudChannel(name, literal));
    }

    default void setChannel(Pair<String, String> pair) {
        this.setChannel(pair.getLeft(), pair.getRight());
    }

    default void setChannel(NbtCompound tag) {
        this.setChannel(new CloudChannel(tag));
    }

    default boolean isPublic() {
        return this.hasChannel() && getChannel().isPublic();
    }

    default boolean isOwned(Entity entity) {
        return entity != null && this.hasChannel() && this.getChannel().isOwned(entity);
    }

    void setBoxInventory(DefaultedList<ItemStack> list);

    default void setInventoryAndChannel(DefaultedList<ItemStack> list, Pair<String, String> pair) {
        setBoxInventory(list);
        setChannel(pair);
    }

    void setName(Text text);

    default void readBoxTag(NbtCompound tag, CallbackInfo info, Consumer<NbtCompound> superRead) {
        if(tag.contains("vw_channel")) {
            this.setBoxInventory(EMPTY);
            superRead.accept(tag);
            setChannel(tag);
            this.setBoxInventory(getCloudBox(getChannel()));
            info.cancel();
        }
    }

    default void writeBoxTag(NbtCompound tag, CallbackInfo info, Consumer<NbtCompound> superWrite) {
        if(hasChannel()) {
            superWrite.accept(tag);
            Inventories.writeNbt(tag, EMPTY, false);
            this.getChannel().writeToNbt(tag);
            info.cancel();
        }
    }

    default void clearCloud() {
        if(hasChannel()) {
            setBoxInventory(EMPTY);
            setName(null);
        }
    }

    default void clearCloud(World world, BlockPos pos) {
        if(world.getBlockEntity(pos) instanceof CloudShulkerBox box) {
            box.clearCloud();
        }
    }

    default void channelCloud(World world, BlockPos pos, ItemStack stack) {
        if(stack.hasNbt()) {
            Pair<String, String> channelPair = CloudChannel.getChannel(stack);
            if(channelPair == null) {
                return;
            }

            getCloudBox(channelPair.getLeft(), box -> {
                if(world.getBlockEntity(pos) instanceof CloudShulkerBox cloudBox) {
                    cloudBox.setInventoryAndChannel(box, channelPair);
                }
            });
        }
    }

    interface BiStacksSupplier {
        List<ItemStack> supply(BlockState state, LootContextParameterSet.Builder builder);
    }

    default void lootCloud(BiStacksSupplier getStacks, BlockState state, LootContextParameterSet.Builder builder, CallbackInfoReturnable<List<ItemStack>> info) {
        LootContextParameterSet parameters = ((LootContextBuilderAccessor)builder).getParameters();
        if(parameters != null && parameters.get(LootContextParameters.BLOCK_ENTITY) instanceof CloudShulkerBox cloudBox) {
            if(!cloudBox.hasChannel()) {
                return;
            }

            if(!cloudBox.isPublic() && !cloudBox.isOwned(parameters.get(LootContextParameters.THIS_ENTITY))) {
                return;
            }

            cloudBox.clearCloud();
            List<ItemStack> loots = getStacks.supply(state, builder);

            loots.forEach(cloudBox.getChannel()::writeToStack);
            info.setReturnValue(loots);
        }
    }
}
