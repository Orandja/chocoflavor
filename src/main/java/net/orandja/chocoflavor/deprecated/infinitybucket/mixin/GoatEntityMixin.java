//package net.orandja.chocoflavor.mods.infinitybucket.mixin;
//
//import net.minecraft.entity.passive.GoatEntity;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.item.ItemStack;
//import net.orandja.chocoflavor.mods.infinitybucket.InfinityBucket;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Redirect;
//
//@Mixin(GoatEntity.class)
//public abstract class GoatEntityMixin implements InfinityBucket {
//
//    @Redirect(method = "interactMob", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemUsage;exchangeStack(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;"))
//    public ItemStack interactMob(ItemStack inputStack, PlayerEntity player, ItemStack outputStack) {
//        return InfinityBucket.handleInfinityBucket(inputStack, player, outputStack);
//    }
//
//}