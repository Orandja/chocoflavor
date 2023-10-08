package net.orandja.chocoflavor.mods.craftingdispenser;

import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.recipe.RecipeType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.orandja.chocoflavor.utils.InventoryUtils;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

public interface CraftingDispenser {

    class DispenserCraftingInventory extends CraftingInventory {

        private final int width;
        private final int height;
        private final DefaultedList<ItemStack> stacks;

        public DispenserCraftingInventory(int width, int height, List<ItemStack> stacks) {
            super(null, width, height);
            this.width = width;
            this.height = height;
            this.stacks = DefaultedList.ofSize(width * height, ItemStack.EMPTY);
            for (int i = 0; i < this.stacks.size(); i++) {
                this.stacks.set(i, stacks.get(i));
            }
        }

        @Override
        public int size() {
            return this.stacks.size();
        }

        @Override
        public boolean isEmpty() {
            for (ItemStack itemStack : this.stacks) {
                if (itemStack.isEmpty()) continue;
                return false;
            }
            return true;
        }

        @Override
        public ItemStack getStack(int slot) {
            return slot >= this.size() ? ItemStack.EMPTY : this.stacks.get(slot);
        }

        @Override
        public void setStack(int slot, ItemStack stack) {
            this.stacks.set(slot, stack);
        }

        @Override
        public ItemStack removeStack(int slot) {
            return Inventories.removeStack(this.stacks, slot);
        }

        @Override
        public ItemStack removeStack(int slot, int amount) {
            return Inventories.splitStack(this.stacks, slot, amount);
        }

        @Override
        public void markDirty() {}

        @Override
        public boolean canPlayerUse(PlayerEntity player) {
            return true;
        }

        @Override
        public void clear() {
            this.stacks.clear();
        }

        @Override
        public int getHeight() {
            return this.height;
        }

        @Override
        public int getWidth() {
            return this.height;
        }

        @Override
        public void provideRecipeInputs(RecipeMatcher finder) {
            this.stacks.forEach(finder::addInput);
        }
    }

    DispenserBehavior BEHAVIOR = new DispenserBehavior() {
        @Override
        public ItemStack dispense(BlockPointer pointer, ItemStack itemStack) {
            pointer.world().syncWorldEvent(1000, pointer.pos(), 0);
            pointer.world().syncWorldEvent(2000, pointer.pos(), pointer.state().get(DispenserBlock.FACING).getId());
            return dispenseSilently(pointer, itemStack);
        }

        public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
            spawnItem(pointer.world(), stack, 6, pointer.state().get(DispenserBlock.FACING), pointer.pos());
            return stack;
        }

        public void spawnItem(World world, ItemStack stack, int offset, Direction direction, BlockPos pos) {
            BlockPos itemPos = pos.offset(direction, 2);
            double yOffset = itemPos.getY() + 0.5 - (direction.getAxis().equals(Direction.Axis.Y) ? 0.125 : 0.15625);
            ItemEntity itemEntity = new ItemEntity(world, itemPos.getX() + 0.5, yOffset, itemPos.getZ() + 0.5, stack);
            Random random = world.getRandom();
            double randomOffset = random.nextDouble() * 0.1 + 0.2;
            itemEntity.setVelocity(
                    random.nextGaussian() * 0.007499999832361937 * offset + direction.getOffsetX() * randomOffset,
                    random.nextGaussian() * 0.007499999832361937 * offset + 0.20000000298023224,
                    random.nextGaussian() * 0.007499999832361937 * offset + direction.getOffsetZ() * randomOffset);
            world.spawnEntity(itemEntity);
        }
    };

    DefaultedList<ItemStack> getInventory();

    default void onBlockDispense(ServerWorld world, BlockPos pos, CallbackInfo info) {
        Direction facing = world.getBlockState(pos).get(DispenserBlock.FACING);
        if(world.getBlockState(pos.offset(facing)) != Blocks.CRAFTING_TABLE.getDefaultState()) {
            return;
        }

        info.cancel();

        if(world.getBlockEntity(pos) instanceof DispenserBlockEntity dispenser && dispenser instanceof CraftingDispenser craftingDispenser) {
            BlockPos outputPos = pos.offset(facing, 2);

            DefaultedList<ItemStack> stacks = craftingDispenser.getInventory();
            DispenserCraftingInventory inventory = new DispenserCraftingInventory(3, 3, stacks);
            Optional<RecipeEntry<CraftingRecipe>> optional = world.getRecipeManager().getFirstMatch(RecipeType.CRAFTING, inventory, world);
            if(optional.isEmpty()) {
                return;
            }

            CraftingRecipe recipe = optional.get().value();
            Inventory outputInventory = HopperBlockEntity.getInventoryAt(world, outputPos);
            ItemStack craftedStack = recipe.craft(inventory, world.getRegistryManager());
            BlockPointer blockPointerImpl = new BlockPointer(world, pos, world.getBlockState(pos), dispenser);

            if(craftedStack == null) {
                return;
            }

            if(outputInventory != null) {
                InventoryUtils.mergeInto(recipe.getResult(world.getRegistryManager()).copy(), outputInventory, it -> craftedStack);
            } else {
                BEHAVIOR.dispense(blockPointerImpl, craftedStack);
            }


            DefaultedList<ItemStack> defaultedList = world.getRecipeManager().getRemainingStacks(RecipeType.CRAFTING, inventory, world);

            for (int i = 0; i < defaultedList.size(); i++) {
                ItemStack invStack = inventory.getStack(i);
                ItemStack itemStack2 = defaultedList.get(i);
                if (!invStack.isEmpty()) {
                    inventory.removeStack(i, 1);
                    invStack = inventory.getStack(i);
                }
                if (!itemStack2.isEmpty()) {
                    if (invStack.isEmpty()) {
                        inventory.setStack(i, itemStack2);
                        stacks.set(i, itemStack2);
                    } else if (invStack.getItem() == itemStack2.getItem() && invStack.getNbt().equals(itemStack2.getNbt())) {
                        itemStack2.increment(invStack.getCount());
                        inventory.setStack(i, itemStack2);
                        stacks.set(i, itemStack2);
                    } else {
                        BEHAVIOR.dispense(blockPointerImpl, itemStack2);
                    }
                }
            }
        }
    }

}
