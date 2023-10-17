package net.orandja.chocoflavor.mods.higherenchantmentlevel.mixin;

import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.screen.*;
import net.orandja.chocoflavor.mods.higherenchantmentlevel.HigherEnchantmentLevel;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("unused")
@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler implements HigherEnchantmentLevel {
    @Shadow @Final private Property levelCost;
    @Unique private PlayerInventory playerInventory;

    public AnvilScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    @Inject(method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/screen/ScreenHandlerContext;)V", at = @At(value = "RETURN"))
    public void getPlayerInventory(int syncId, PlayerInventory inventory, ScreenHandlerContext context, CallbackInfo info) {
        this.playerInventory = inventory;
    }

    @Redirect(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getMaxLevel()I"))
    public int maxLevel(Enchantment enchantment) {
        return getMaxLevel(enchantment);
    }

    @ModifyConstant(method = "updateResult", constant = @Constant(intValue = 40))
    private int injected(int value) {
        return Integer.MAX_VALUE;
    }

    @Override
    public void sendContentUpdates() {
        ItemStack before = this.output.getStack(0);
        int beforeLevel = this.levelCost.get();
        if(this.levelCost.get() >= 40) {
            ItemStack withlore = before.copy();

            NbtCompound tag = withlore.getOrCreateNbt();
            NbtCompound display;
            if(!tag.contains("display")) {
                display = new NbtCompound();
                tag.put("display", display);
            } else {
                display = tag.getCompound("display");
            }

            NbtList lore;
            if(!display.contains("Lore")) {
                lore = new NbtList();
                display.put("Lore", lore);
            } else {
                lore = display.getList("Lore", NbtElement.STRING_TYPE);
            }

            lore.add(NbtString.of("{\"color\": \""+ (playerInventory.player.experienceLevel >= levelCost.get() ? "green" : "red") +"\",\"translate\": \"container.repair.cost\",\"with\": [\""+ levelCost.get() +"\"]}"));
            this.output.setStack(0, withlore);

            this.levelCost.set(0);
        }

        super.sendContentUpdates();

        this.output.setStack(0, before);
        this.levelCost.set(beforeLevel);
    }
}