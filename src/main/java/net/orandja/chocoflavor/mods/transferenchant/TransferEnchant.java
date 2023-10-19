package net.orandja.chocoflavor.mods.transferenchant;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.orandja.chocoflavor.utils.NBTUtils;
import net.orandja.chocoflavor.utils.Settings;
import net.orandja.chocoflavor.utils.StackUtils;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

@SuppressWarnings("ConstantConditions")
public interface TransferEnchant {
    class TransferEnchantSlot extends Slot {

        public TransferEnchantSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return stack.isDamageable() || stack.isOf(Items.ENCHANTED_BOOK) || StackUtils.hasAnyEnchantments(stack) || stack.isOf(Items.BOOK);
        }
    }

    class TransferEnchantOutputSlot extends Slot {

        private final Inventory result;
        private final Inventory input;
        private final ScreenHandlerContext context;

        public TransferEnchantOutputSlot(Inventory input, Inventory result, ScreenHandlerContext context) {
            super(result, 2, 129, 34);
            this.input = input;
            this.result = result;
            this.context = context;
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return false;
        }

        @Override
        public void onTakeItem(PlayerEntity player, ItemStack stack) {
            if (getBookAndToolSlots((bookSlot, toolSlot) -> {
                if (player.experienceLevel < transferCost.getValue()) {
                    result.setStack(0, ItemStack.EMPTY);
                    return;
                }

                player.addExperienceLevels(-transferCost.getValue());

                input.getStack(bookSlot).decrement(1);
                if (destroyItem.getValue()) {
                    input.setStack(toolSlot, ItemStack.EMPTY);
                    return;
                }

                final AtomicReference<ItemStack> toolStack = new AtomicReference<>(input.getStack(toolSlot));
                stack.getNbt().getList("StoredEnchantments", NbtElement.COMPOUND_TYPE).forEach(it_ -> {
                    String enchantmentKey = toolStack.get().getItem() == Items.ENCHANTED_BOOK ? "StoredEnchantments" : "Enchantments";
                    NbtList enchantmentList = toolStack.get().getNbt().getList(enchantmentKey, NbtElement.COMPOUND_TYPE);

                    if (it_ instanceof NbtCompound it) {
                        String enchant = it.getString("id");
                        short level = it.getShort("lvl");

                        for (int i = 0; i < enchantmentList.size(); i++) {
                            NbtCompound toolTag = enchantmentList.getCompound(i);
                            String toolEnchant = toolTag.getString("id");
                            short toolLevel = toolTag.getShort("lvl");
                            if (toolEnchant.equals(enchant) && toolLevel == level) {
                                enchantmentList.remove(i);
                                break;
                            }
                        }

                        if (enchantmentList.size() == 0) {
                            if (toolStack.get().getItem() == Items.ENCHANTED_BOOK) {
                                toolStack.set(new ItemStack(Items.BOOK, 1));
                            } else {
                                toolStack.get().removeSubNbt(enchantmentKey);
                            }
                        }
                    }
                });

                toolStack.get().setRepairCost((toolStack.get().getRepairCost() - 1) / 2);
                input.setStack(toolSlot, toolStack.get());
            })) {
                return;
            }

            context.run((world, pos) -> {
                if (world instanceof ServerWorld serverWorld) {
                    ExperienceOrbEntity.spawn(serverWorld, Vec3d.ofCenter(pos), this.getExperience(world));
                }
                world.syncWorldEvent(1042, pos, 0);
            });
            input.setStack(0, ItemStack.EMPTY);
            input.setStack(1, ItemStack.EMPTY);
        }

        private int getExperience(World world) {
            int i = this.getExperience(input.getStack(0)) + this.getExperience(input.getStack(1));
            if (i > 0) {
                int j = (int) Math.ceil(((double) i) / 2.0D);
                return j + world.getRandom().nextInt(j);
            }
            return 0;
        }

        private int getExperience(ItemStack stack) {
            int i = 0;
            Map<Enchantment, Integer> map = EnchantmentHelper.get(stack);
            for (Enchantment enchantment : map.keySet()) {
                if (!enchantment.isCursed()) {
                    i += enchantment.getMinPower(map.get(enchantment));
                }
            }

            return i;
        }

        private int getSlot(Predicate<ItemStack> predicate) {
            if (predicate.test(input.getStack(0))) {
                return 0;
            }
            if (predicate.test(input.getStack(1))) {
                return 1;
            }
            return -1;
        }

        public boolean getBookAndToolSlots(BiConsumer<Integer, Integer> consumer) {
            int bookSlot = getSlot(stack -> stack.isOf(Items.BOOK));
            int toolSlot = getSlot(StackUtils::hasAnyEnchantments);
            if (bookSlot > -1 && toolSlot > -1) {
                consumer.accept(bookSlot, toolSlot);
                return true;
            }

            return false;
        }
    }

//    int cost = 8;
    Settings.Number<Integer> transferCost = new Settings.Number<Integer>("Transfer Cost", 8, Integer::parseInt);
//    boolean destroyItem = false;
    Settings.Boolean destroyItem = new Settings.Boolean("Destroy Item", false);

    Inventory getInput();

    Inventory getResult();

    TransferEnchantOutputSlot getOutputSlot();

    TransferEnchantOutputSlot vw$setOutputSlot(TransferEnchantOutputSlot outputSlot);

    PlayerInventory getPlayerInventory();

    void setPlayerInventory(PlayerInventory playerInventory);

    default void onInit(PlayerInventory playerInventory, ScreenHandlerContext context) {
        this.setPlayerInventory(playerInventory);


        vw$replaceSlot(new TransferEnchantSlot(getInput(), 0, 49, 19), 0);
        vw$replaceSlot(new TransferEnchantSlot(getInput(), 1, 49, 40), 1);

        vw$replaceSlot(vw$setOutputSlot(new TransferEnchantOutputSlot(getInput(), getResult(), context)), 2);
    }

    void vw$replaceSlot(Slot slot, int id);

    void vw$updateContents();

    default void updateTransferResult(CallbackInfo info) {
        this.getOutputSlot().getBookAndToolSlots((bookSlot, toolSlot) -> {
            info.cancel();
            if (getPlayerInventory().player.experienceLevel < transferCost.getValue()) {
                getResult().setStack(0, ItemStack.EMPTY);
                return;
            }

            ItemStack outputStack = new ItemStack(Items.ENCHANTED_BOOK, 1);
            ItemStack toolStack = getInput().getStack(toolSlot);
            String enchantmentKey = toolStack.getItem() == Items.ENCHANTED_BOOK ? "StoredEnchantments" : "Enchantments";
            int key = getPlayerInventory().player.getRandom().nextBetween(0, toolStack.getNbt().getList(enchantmentKey, NbtElement.COMPOUND_TYPE).size() - 1);
            NbtCompound tag = outputStack.getOrCreateNbt();
            NBTUtils.getOrCompute(tag, "StoredEnchantments", id -> tag.getList(id, NbtElement.COMPOUND_TYPE), NbtList::new, list -> {
                list.add(toolStack.getNbt().getList(enchantmentKey, NbtElement.COMPOUND_TYPE).get(key).copy());
            });
            outputStack.setRepairCost((toolStack.getRepairCost() - 1) / 2);
            getResult().setStack(0, outputStack);
            this.vw$updateContents();
        });
    }
}
