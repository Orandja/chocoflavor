package net.orandja.chocoflavor;

import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.orandja.chocoflavor.inventory.DispenserCraftingInventory;
import net.orandja.chocoflavor.utils.InventoryUtils;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

public class ChocoDispensers {

    public static DispenserBehavior CRAFTING_BEHAVIOR = new DispenserBehavior() {
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

    public static void dispense(ServerWorld world, BlockPos pos, CallbackInfo info) {
        Direction facing = world.getBlockState(pos).get(DispenserBlock.FACING);
        if(world.getBlockState(pos.offset(facing)) != Blocks.CRAFTING_TABLE.getDefaultState()) {
            return;
        }

        info.cancel();

        if(world.getBlockEntity(pos) instanceof DispenserBlockEntity dispenser && dispenser instanceof Handler craftingDispenser) {
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
                CRAFTING_BEHAVIOR.dispense(blockPointerImpl, craftedStack);
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
                        CRAFTING_BEHAVIOR.dispense(blockPointerImpl, itemStack2);
                    }
                }
            }
        }
    }

    public interface Handler {
        DefaultedList<ItemStack> getInventory();
    }

}
