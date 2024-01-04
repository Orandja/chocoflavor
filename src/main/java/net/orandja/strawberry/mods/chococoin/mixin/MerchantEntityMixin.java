package net.orandja.strawberry.mods.chococoin.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.world.World;
import net.orandja.strawberry.mods.chococoin.CoinMerchantEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MerchantEntity.class)
public abstract class MerchantEntityMixin extends PassiveEntity implements CoinMerchantEntity {

    private Runnable run;

    @Shadow public abstract SimpleInventory getInventory();

    private int shouldResend;

    protected MerchantEntityMixin(EntityType<? extends PassiveEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if(this.shouldResend > 0) {
            shouldResend--;
            if(this.shouldResend == 0 && this.run != null) {
                this.run.run();
                this.run = null;
            }
        }
    }

    @Override
    public void resend(Runnable run) {
        this.shouldResend = 10;
        this.run = run;
    }
}
