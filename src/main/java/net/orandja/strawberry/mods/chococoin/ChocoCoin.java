package net.orandja.strawberry.mods.chococoin;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.orandja.strawberry.mods.chococoin.item.CoinItem;

import java.util.ArrayList;
import java.util.List;

public abstract class ChocoCoin {

    public static class CoinUtils {
        public static final double EMERALD_RATE = 8;

        public static boolean isCoin(ItemStack stack) {
            return stack.getItem() instanceof CoinItem;
        }

        public static int getCoinValue(ItemStack stack) {
            return isCoin(stack) ? ((CoinItem)stack.getItem()).getValue(stack) : 0;
        }

        public static boolean isEmerald(ItemStack stack) {
            return stack.isOf(Items.EMERALD);
        }

        public static int getChocoUnit(ItemStack... stacks) {
            int value = 0;
            for (ItemStack stack : stacks) {
                if(isEmerald(stack)) {
                    value += (int) (stack.getCount() * EMERALD_RATE);
                    continue;
                }

                if(isCoin(stack)) {
                    value += getCoinValue(stack);
                }
            }
            return value;
        }

        public static List<List<ItemStack>> possibleStacks(int value) {
            List<List<ItemStack>> stacks = new ArrayList<>();

            for (int i = 0; i < ASC_COINS.length; i++) {
                CoinItem coin = ASC_COINS[i];
                if(value <= coin.getMax() && (value % coin.getValue() == 0 || Math.ceil((value * 1.0D) / coin.getValue()) == 1)) {
                    stacks.add(List.of(new ItemStack(coin, (int) Math.ceil((value * 1.0D) / coin.getValue()))));
                }
            }

            for (int i = 0; i < ASC_COINS.length; i++) {
                if(i == ASC_COINS.length - 1) break;

                CoinItem coin = ASC_COINS[i];
                CoinItem coinB = ASC_COINS[i+1];
                // if the value is higher than the maximum available with two coins.
                // if the value can be represented by the better coin.
                if(value > (coinB.getMax() + coin.getMax()) || value % coinB.getValue() == 0) continue;
                int amountB = (int) Math.floor((value * 1.0D) / coinB.getValue());
                while(amountB > 0) {
                    int amountA = value % (amountB * coinB.getValue());
                    if(amountA > coin.getMax()) {
                        break;
                    }
                    stacks.add(List.of(new ItemStack(coinB, amountB), new ItemStack(coin, amountA)));
                    amountB--;
                }
            }

            return stacks;
        }

        public static List<ItemStack> convertToStacks(int chocoUnit) {
            List<ItemStack> stacks = new ArrayList<>();

            String coins = Integer.toString(chocoUnit, 9);
            /* 11 6 8 */

            for (int i = 0; i < ASC_COINS.length; i++) {
                CoinItem coin = ASC_COINS[i];
                if(i == ASC_COINS.length - 1) {
                    int count = getCountAt(coins, 0, coins.length() - 2);
                    if(count > 0) {
                        stacks.add(new ItemStack(coin, count));
                    }
                    continue;
                }
                int count = getCountAt(coins, coins.length() - i - 1, coins.length() - i);
                if(count > 0) {
                    stacks.add(new ItemStack(coin, count));
                }
            }

            return stacks;
        }

        public static int getCountAt(String input, int digitStart, int digitEnd) {
            if(input.length() >= digitEnd && (digitEnd - digitStart > 0)) {
                try {
                    return Integer.parseInt(input.substring(digitStart, digitEnd));
                } catch(Exception e) { }
            }
            return 0;
        }
    }

    public interface Handler {

        int getChocoUnit();

        ItemStack getSellItemBackup();
    }

    public static CoinItem CHOCOCOIN_COPPER;
    public static CoinItem CHOCOCOIN_EMERALD;
    public static CoinItem CHOCOCOIN_GOLD;

    public static CoinItem[] ASC_COINS;
    public static CoinItem[] DESC_COINS;

    public static final double RATE = 8;

    /*
        GOLD = 81, 5184
        EMERALD = 9, 576
        COPPER = 1, 64
     */

    public static final int MAX_EMERALD = 576;
    public static final int MAX_COPPER = 64;
    public static final int GOLD_VALUE = 81;
    public static final int EMERALD_VALUE = 9;

    public static void beforeLaunch() {
        Items.register("chococoin_copper", CHOCOCOIN_COPPER = new CoinItem(Items.EMERALD, 1, null, new Item.Settings()));
        Items.register("chococoin_emerald", CHOCOCOIN_EMERALD = new CoinItem(Items.EMERALD, 2, CHOCOCOIN_COPPER, new Item.Settings()));
        Items.register("chococoin_gold", CHOCOCOIN_GOLD = new CoinItem(Items.EMERALD, 3, CHOCOCOIN_EMERALD, new Item.Settings()));
        ASC_COINS = new CoinItem[] { CHOCOCOIN_COPPER, CHOCOCOIN_EMERALD, CHOCOCOIN_GOLD };
        DESC_COINS = new CoinItem[] { CHOCOCOIN_GOLD, CHOCOCOIN_EMERALD, CHOCOCOIN_COPPER };
    }

    public static ItemStack[] emeraldToChoco(ItemStack firstStack, ItemStack secondStack) {
        int availableSlots = (isEmeraldOrEmpty(firstStack) ? 1 : 0) + (isEmeraldOrEmpty(secondStack) ? 1 : 0);
        long value = getEmeraldValue(firstStack) + getEmeraldValue(secondStack);
        ItemStack[] coinStacks = getCoinStacks(availableSlots, Long.toString(value, 9));
        return new ItemStack[] {
            isEmeraldOrEmpty(firstStack) && coinStacks.length > 0 ? coinStacks[0] : firstStack,
            isEmeraldOrEmpty(secondStack) && coinStacks.length > 1 ? coinStacks[1] : secondStack
        };
    }

    public static int getEmeraldValue(ItemStack stack) {
        if (stack.getItem() instanceof CoinItem coinItem) {
            return coinItem.getValue(stack);
        }
        return stack.getItem() == Items.EMERALD ? (int) (stack.getCount() * RATE) : 0;
    }

    public static int getCoinValue(ItemStack stack) {
        return stack.getItem() instanceof CoinItem coinItem ? coinItem.getValue(stack) : 0;
    }

    public static boolean isEmeraldOrEmpty(ItemStack stack) {
        return stack.isEmpty() || stack.getItem() == Items.EMERALD;
    }

    public static void main(String... args) {
        System.out.println(Long.toString((81 * 64) - 10, 9));
        System.out.println(Long.parseLong("7000", 9) / 81);
    }

    public static ItemStack[] getCoinStacks(int availableSlots, String base9) {
        ItemStack[] stacks = new ItemStack[availableSlots];
        int cursor = 0;
        for(int i = 0; i < availableSlots; i++) {
            stacks[i] = ItemStack.EMPTY;
            if(base9.length() <= i) {
                cursor = availableSlots;
                continue;
            }
            int count = (int) Long.parseLong(base9.substring(base9.length() - 1 - i, base9.length() - i));
            if(count > 0) {
                stacks[cursor++] = new ItemStack(ChocoCoin.DESC_COINS[i], count);
            }
        }

        if(cursor < availableSlots) {
            long count = Long.parseLong(base9.substring(0, 2), 9) / ChocoCoin.DESC_COINS[0].getValue();
            stacks[cursor] = new ItemStack(ChocoCoin.DESC_COINS[0], (int)count);
        }

        return stacks;

    }

    public static ItemStack[] getChocoCoinPricing(ItemStack firstStack, ItemStack secondStack) {
        ItemStack[] stacks = new ItemStack[]{firstStack, secondStack};
        int value = getEmeraldValue(firstStack) + getEmeraldValue(secondStack);
        boolean second = false;

        if (isEmeraldOrEmpty(secondStack) && isEmeraldOrEmpty(firstStack)) {
            if (value > MAX_EMERALD) {
                int goldCoins = Math.floorDiv(value, GOLD_VALUE);
                value -= goldCoins * GOLD_VALUE;
                stacks[second ? 1 : 0] = new ItemStack(CHOCOCOIN_GOLD, goldCoins);

                second = true;
                if (value == 0) {
                    return stacks;
                }
            }

            if (value > MAX_COPPER) {
                int emeraldCoins = Math.floorDiv(value, EMERALD_VALUE);
                value -= emeraldCoins * EMERALD_VALUE;
                stacks[second ? 1 : 0] = new ItemStack(CHOCOCOIN_EMERALD, emeraldCoins);
                if (second || value == 0) {
                    return stacks;
                }
            } else {
                stacks[second ? 1 : 0] = new ItemStack(CHOCOCOIN_COPPER, value);
                if (second || value == 0) {
                    return stacks;
                }
            }
        }

        if (firstStack.getItem() == Items.EMERALD || (second = secondStack.getItem() == Items.EMERALD)) {
            if (value > MAX_EMERALD) {
                stacks[second ? 1 : 0] = new ItemStack(CHOCOCOIN_GOLD, Math.floorDiv(value, GOLD_VALUE));
            } else if (value > MAX_COPPER) {
                stacks[second ? 1 : 0] = new ItemStack(CHOCOCOIN_EMERALD, Math.floorDiv(value, EMERALD_VALUE));
            } else {
                stacks[second ? 1 : 0] = new ItemStack(CHOCOCOIN_COPPER, value);
            }
            return stacks;
        }

        return stacks;
    }
}
