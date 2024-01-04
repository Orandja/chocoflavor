package net.orandja.chocoflavor;

import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.orandja.chocoflavor.enchantment.EnchantmentDictionary;
import net.orandja.chocoflavor.enchantment.EnchantmentLevelRegistry;
import net.orandja.chocoflavor.enchantment.ItemEnchantmentsRegistry;
import net.orandja.chocoflavor.inventory.TransferEnchantOutputSlot;
import net.orandja.chocoflavor.inventory.TransferEnchantSlot;
import net.orandja.chocoflavor.accessor.LootContextParameterSetAccessor;
import net.orandja.chocoflavor.utils.GlobalUtils;
import net.orandja.chocoflavor.utils.NBTUtils;
import net.orandja.chocoflavor.utils.Settings;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChocoEnchantments {
    private ChocoEnchantments() {}

    public static final Settings.Number<Integer> TRANSFER_COST = new Settings.Number<Integer>("Transfer Cost", 8, Integer::parseInt);
    public static final Settings.Boolean DESTROY_ITEM = new Settings.Boolean("Destroy Item", false);

    public static final Map<Item, ItemEnchantmentsRegistry> itemRegistry = new HashMap<>();
    public static ItemEnchantmentsRegistry getRegistry(Item item) {
        return itemRegistry.get(item);
    }

    public static ItemEnchantmentsRegistry createRegistry(Item item) {
        return itemRegistry.computeIfAbsent(item, ItemEnchantmentsRegistry::new);
    }

    public static final Map<Enchantment, EnchantmentLevelRegistry> levelRegistry = new HashMap<>() {{
        this.put(Enchantments.BANE_OF_ARTHROPODS, new EnchantmentLevelRegistry(Enchantments.BANE_OF_ARTHROPODS).setMaxAnvilLevel(10).setMaxLevel(10));
        this.put(Enchantments.BLAST_PROTECTION, new EnchantmentLevelRegistry(Enchantments.BLAST_PROTECTION).setMaxAnvilLevel(10).setMaxLevel(10));
        this.put(Enchantments.DEPTH_STRIDER, new EnchantmentLevelRegistry(Enchantments.DEPTH_STRIDER).setMaxAnvilLevel(10).setMaxLevel(10));
        this.put(Enchantments.EFFICIENCY, new EnchantmentLevelRegistry(Enchantments.EFFICIENCY).setMaxAnvilLevel(10).setMaxLevel(10));
        this.put(Enchantments.FEATHER_FALLING, new EnchantmentLevelRegistry(Enchantments.FEATHER_FALLING).setMaxAnvilLevel(10).setMaxLevel(10));
        this.put(Enchantments.FIRE_ASPECT, new EnchantmentLevelRegistry(Enchantments.FIRE_ASPECT).setMaxAnvilLevel(10).setMaxLevel(10));
        this.put(Enchantments.FIRE_PROTECTION, new EnchantmentLevelRegistry(Enchantments.FIRE_PROTECTION).setMaxAnvilLevel(10).setMaxLevel(10));
        this.put(Enchantments.FORTUNE, new EnchantmentLevelRegistry(Enchantments.FORTUNE).setMaxAnvilLevel(10).setMaxLevel(10));
        this.put(Enchantments.FROST_WALKER, new EnchantmentLevelRegistry(Enchantments.FROST_WALKER).setMaxAnvilLevel(10).setMaxLevel(10));
        this.put(Enchantments.IMPALING, new EnchantmentLevelRegistry(Enchantments.IMPALING).setMaxAnvilLevel(10).setMaxLevel(10));
        this.put(Enchantments.KNOCKBACK, new EnchantmentLevelRegistry(Enchantments.KNOCKBACK).setMaxAnvilLevel(10).setMaxLevel(10));
        this.put(Enchantments.LOOTING, new EnchantmentLevelRegistry(Enchantments.LOOTING).setMaxAnvilLevel(10).setMaxLevel(10));
        this.put(Enchantments.LOYALTY, new EnchantmentLevelRegistry(Enchantments.LOYALTY).setMaxAnvilLevel(10).setMaxLevel(10));
        this.put(Enchantments.LUCK_OF_THE_SEA, new EnchantmentLevelRegistry(Enchantments.LUCK_OF_THE_SEA).setMaxAnvilLevel(10).setMaxLevel(10));
        this.put(Enchantments.LURE, new EnchantmentLevelRegistry(Enchantments.LURE).setMaxAnvilLevel(10).setMaxLevel(10));
        this.put(Enchantments.MENDING, new EnchantmentLevelRegistry(Enchantments.MENDING).setMaxAnvilLevel(10).setMaxLevel(10));
        this.put(Enchantments.PIERCING, new EnchantmentLevelRegistry(Enchantments.PIERCING).setMaxAnvilLevel(10).setMaxLevel(10));
        this.put(Enchantments.POWER, new EnchantmentLevelRegistry(Enchantments.POWER).setMaxAnvilLevel(10).setMaxLevel(10));
        this.put(Enchantments.PROJECTILE_PROTECTION, new EnchantmentLevelRegistry(Enchantments.PROJECTILE_PROTECTION).setMaxAnvilLevel(10).setMaxLevel(10));
        this.put(Enchantments.PROTECTION, new EnchantmentLevelRegistry(Enchantments.PROTECTION).setMaxAnvilLevel(10).setMaxLevel(10));
        this.put(Enchantments.PUNCH, new EnchantmentLevelRegistry(Enchantments.PUNCH).setMaxAnvilLevel(10).setMaxLevel(10));
        this.put(Enchantments.QUICK_CHARGE, new EnchantmentLevelRegistry(Enchantments.QUICK_CHARGE).setMaxAnvilLevel(10).setMaxLevel(10));
        this.put(Enchantments.RESPIRATION, new EnchantmentLevelRegistry(Enchantments.RESPIRATION).setMaxAnvilLevel(10).setMaxLevel(10));
        this.put(Enchantments.RIPTIDE, new EnchantmentLevelRegistry(Enchantments.RIPTIDE).setMaxAnvilLevel(10).setMaxLevel(10));
        this.put(Enchantments.SHARPNESS, new EnchantmentLevelRegistry(Enchantments.SHARPNESS).setMaxAnvilLevel(10).setMaxLevel(10));
        this.put(Enchantments.SMITE, new EnchantmentLevelRegistry(Enchantments.SMITE).setMaxAnvilLevel(10).setMaxLevel(10));
        this.put(Enchantments.SOUL_SPEED, new EnchantmentLevelRegistry(Enchantments.SOUL_SPEED).setMaxAnvilLevel(10).setMaxLevel(10));
        this.put(Enchantments.SWEEPING, new EnchantmentLevelRegistry(Enchantments.SWEEPING).setMaxAnvilLevel(10).setMaxLevel(10));
        this.put(Enchantments.SWIFT_SNEAK, new EnchantmentLevelRegistry(Enchantments.SWIFT_SNEAK).setMaxAnvilLevel(10).setMaxLevel(10));
        this.put(Enchantments.THORNS, new EnchantmentLevelRegistry(Enchantments.THORNS).setMaxAnvilLevel(10).setMaxLevel(10));
        this.put(Enchantments.UNBREAKING, new EnchantmentLevelRegistry(Enchantments.UNBREAKING).setMaxAnvilLevel(10).setMaxLevel(10));
    }};

    public interface LevelHandler {
        default EnchantmentLevelRegistry getLevelRegistry(Enchantment enchantment) {
            return levelRegistry.get(enchantment);
        }
    }

    public interface ItemHandler {
        default ItemEnchantmentsRegistry getItemRegistry() {
            return GlobalUtils.runAs(this, Item.class, ChocoEnchantments::getRegistry);
        }
        default ItemEnchantmentsRegistry getItemRegistry(Item item) {
            return ChocoEnchantments.getRegistry(item);
        }
        default ItemEnchantmentsRegistry getItemRegistry(ItemStack stack) {
            return this.getItemRegistry(stack.getItem());
        }
    }

    public interface BlockHandler {
        EnchantmentDictionary getDictionary();
        EnchantmentDictionary createDictionary();

        default void onBlockPlaced(World world, BlockPos pos, BlockState state, ItemStack stack) {
            if(stack.hasNbt() && stack.getNbt().contains(ItemStack.ENCHANTMENTS_KEY)) {
                if(state.getBlock() instanceof BlockWithEntity && world.getBlockEntity(pos) instanceof BlockHandler handler) {
                    handler.getDictionary().loadFromNbt(stack.getNbt());
                    handler.applyEnchantments();
                }
            }
        }

        default void applyEnchantments() {}

        default <T extends BlockEntity> BlockEntityTicker<T> getCustomTicker() {
            return null;
        }

        default List<ItemStack> enchantLoots(List<ItemStack> drops, BlockState state, LootContextParameterSet.Builder builder) {
            Map<LootContextParameter<?>, Object> parameters = ((LootContextParameterSetAccessor)builder).getParameters();
            if(parameters != null && parameters.get(LootContextParameters.BLOCK_ENTITY) instanceof BlockHandler blockWithEnchantment) {
                drops.forEach(blockWithEnchantment.getDictionary()::saveToNbt);
            }

            return drops;
        }

        default boolean hasCustomTicker() {
            return this.getCustomTicker() != null;
        }
    }


    public interface TransferHandler {
        Inventory getInput();

        Inventory getResult();

        TransferEnchantOutputSlot getOutputSlot();

        TransferEnchantOutputSlot setOutputSlot(TransferEnchantOutputSlot outputSlot);

        PlayerInventory getPlayerInventory();

        void setPlayerInventory(PlayerInventory playerInventory);

        default void onInit(PlayerInventory playerInventory, ScreenHandlerContext context) {
            this.setPlayerInventory(playerInventory);


            replaceSlot(new TransferEnchantSlot(getInput(), 0, 49, 19), 0);
            replaceSlot(new TransferEnchantSlot(getInput(), 1, 49, 40), 1);

            replaceSlot(setOutputSlot(new TransferEnchantOutputSlot(getInput(), getResult(), context)), 2);
        }

        void replaceSlot(Slot slot, int id);

        void forceUpdateContents();

        default void updateTransferResult(CallbackInfo info) {
            this.getOutputSlot().getBookAndToolSlots((bookSlot, toolSlot) -> {
                info.cancel();
                if (getPlayerInventory().player.experienceLevel < TRANSFER_COST.getValue()) {
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
                this.forceUpdateContents();
            });
        }
    }
}
