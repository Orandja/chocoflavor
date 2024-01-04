package net.orandja.chocoflavor.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.screen.*;
import net.orandja.chocoflavor.ChocoEnchantments;
import net.orandja.chocoflavor.utils.GlobalUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
        import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("unused")
@Mixin(AnvilScreenHandler.class)
public abstract class ChocoEnchantments_AnvilScreenHandlerMixin extends ForgingScreenHandler implements ChocoEnchantments.LevelHandler, ChocoEnchantments.ItemHandler {
    @Shadow @Final private Property levelCost;
    @Unique private PlayerInventory playerInventory;

    public ChocoEnchantments_AnvilScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    @Inject(method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/screen/ScreenHandlerContext;)V", at = @At(value = "RETURN"))
    public void getPlayerInventory(int syncId, PlayerInventory inventory, ScreenHandlerContext context, CallbackInfo info) {
        this.playerInventory = inventory;
    }

    @Redirect(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getMaxLevel()I"))
    public int maxLevel(Enchantment enchantment) {
        return getLevelRegistry(enchantment).getMaxAnvilLevel();
    }

    @Redirect(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;isAcceptableItem(Lnet/minecraft/item/ItemStack;)Z"))
    public boolean isAcceptableItem(Enchantment enchantment, ItemStack stack) {
        return GlobalUtils.runOrDefault(this.getItemRegistry(stack), enchantment.isAcceptableItem(stack), it -> it.isAllowedInAnvil(stack, enchantment));
    }

    @ModifyConstant(method = "updateResult", constant = @Constant(intValue = 40))
    private int injected(int value) {
        return Integer.MAX_VALUE;
    }

    @Override
    public void sendContentUpdates() {
        ItemStack stackBeforeLore = this.output.getStack(0);
        int beforeLevel = this.levelCost.get();
        if(this.levelCost.get() >= 40) {
            ItemStack stackWithLore = stackBeforeLore.copy();

            NbtCompound display = GlobalUtils.run(stackWithLore.getOrCreateNbt(), tag -> {
                return tag.contains("display") ?
                        tag.getCompound("display") :
                        GlobalUtils.apply(new NbtCompound(), it -> tag.put("display", it));
            });

            NbtList lore = display.contains("Lore") ?
                    display.getList("Lore", NbtElement.STRING_TYPE) :
                    GlobalUtils.apply(new NbtList(), it -> display.put("Lore", it));

            lore.add(NbtString.of("{\"color\": \""+ (playerInventory.player.experienceLevel >= levelCost.get() ? "green" : "red") +"\",\"translate\": \"container.repair.cost\",\"with\": [\""+ levelCost.get() +"\"]}"));
            this.output.setStack(0, stackWithLore);

            this.levelCost.set(0);
        }

        super.sendContentUpdates();

        this.output.setStack(0, stackBeforeLore);
        this.levelCost.set(beforeLevel);
    }
}