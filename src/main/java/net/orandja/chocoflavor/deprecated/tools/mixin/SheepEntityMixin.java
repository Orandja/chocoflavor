//package net.orandja.chocoflavor.mods.tools.mixin;
//
//import net.minecraft.enchantment.EnchantmentHelper;
//import net.minecraft.enchantment.Enchantments;
//import net.minecraft.entity.EntityType;
//import net.minecraft.entity.ItemEntity;
//import net.minecraft.entity.passive.AnimalEntity;
//import net.minecraft.entity.passive.SheepEntity;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.item.ItemConvertible;
//import net.minecraft.sound.SoundCategory;
//import net.minecraft.sound.SoundEvents;
//import net.minecraft.util.DyeColor;
//import net.minecraft.util.Hand;
//import net.minecraft.world.World;
//import org.spongepowered.asm.mixin.Final;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Shadow;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Redirect;
//
//import java.util.Map;
//
//@Mixin(SheepEntity.class)
//public abstract class SheepEntityMixin extends AnimalEntity {
//
//    @Shadow public abstract void setSheared(boolean sheared);
//
//    @Shadow @Final private static Map<DyeColor, ItemConvertible> DROPS;
//
//    @Shadow public abstract DyeColor getColor();
//
//    protected SheepEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
//        super(entityType, world);
//    }
//
//    @Redirect(method = "interactMob", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/SheepEntity;sheared(Lnet/minecraft/sound/SoundCategory;)V"))
//    public void onSheared(SheepEntity instance, SoundCategory shearedSoundCategory, PlayerEntity player, Hand hand) {
//        this.getWorld().playSoundFromEntity(null, this, SoundEvents.ENTITY_SHEEP_SHEAR, shearedSoundCategory, 1.0f, 1.0f);
//        this.setSheared(true);
//        int i = 1 + this.random.nextInt(3 + EnchantmentHelper.getLevel(Enchantments.FORTUNE, player.getStackInHand(hand)));
//        for (int j = 0; j < i; ++j) {
//            ItemEntity itemEntity = this.dropItem(DROPS.get(this.getColor()), 1);
//            if (itemEntity == null) continue;
//            itemEntity.setVelocity(itemEntity.getVelocity().add((this.random.nextFloat() - this.random.nextFloat()) * 0.1f, this.random.nextFloat() * 0.05f, (this.random.nextFloat() - this.random.nextFloat()) * 0.1f));
//        }
//    }
//
//}
