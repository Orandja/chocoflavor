package net.orandja.chocoflavor.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.orandja.chocoflavor.utils.GlobalUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(CrossbowItem.class)
public class ChocoEnchantments_CrossbowItemMixin {

    @Inject(method = "createArrow", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void addPowerToArrow(World world, LivingEntity entity, ItemStack crossbow, ItemStack arrow, CallbackInfoReturnable<PersistentProjectileEntity> cir, ArrowItem arrowItem, PersistentProjectileEntity persistentProjectileEntity) {
        int j;
        if ((j = EnchantmentHelper.getLevel(Enchantments.POWER, crossbow)) > 0) {
            persistentProjectileEntity.setDamage(persistentProjectileEntity.getDamage() + (double)j * 0.5 + 0.5);
        }
    }

    @Redirect(method = "loadProjectile", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;split(I)Lnet/minecraft/item/ItemStack;"))
    private static ItemStack infinityCheck(ItemStack instance, int amount, LivingEntity shooter, ItemStack crossbow, ItemStack projectile, boolean simulated, boolean creative) {
        if(EnchantmentHelper.getLevel(Enchantments.INFINITY, crossbow) == 0) {
            return instance.split(1);
        }
        if(shooter instanceof ServerPlayerEntity serverPlayer) {
            int slot;
            if((slot = serverPlayer.getInventory().getSlotWithStack(projectile)) > -1)  {
                serverPlayer.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(ScreenHandlerSlotUpdateS2CPacket.UPDATE_PLAYER_INVENTORY_SYNC_ID, 0, slot, projectile));
            }
        }
        return instance;
    }

}
