package net.orandja.chocoflavor.mods.animaltemptation;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicates;

import java.util.EnumSet;
import java.util.function.Predicate;

public class TemptItemGoal extends Goal {

    private static final int COOLDOWN = 40;
    private static final double INTERSECT_DISTANCE = 0.75D;

    private final AnimalEntity mob;
    private final double speed;
    private final Predicate<ItemStack> foodPredicate;
    private final double offsetY;
    private final StackHandler stackHandler;

    public interface StackHandler {
        ItemStack affect(ItemStack stack);
    }

    static final StackHandler defaultHandler = it -> {
        it.decrement(1);
        return it;
    };

    ItemEntity itemEntity = null;
    private int cooldown = 0;
    boolean active = false;

    public TemptItemGoal(AnimalEntity mob, double speed, Predicate<ItemStack> foodPredicate) {
        this(mob, speed, foodPredicate, 1.0D, defaultHandler);
    }

    public TemptItemGoal(AnimalEntity mob, double speed, Predicate<ItemStack> foodPredicate, double offsetY) {
        this(mob, speed, foodPredicate, offsetY, defaultHandler);
    }

    public TemptItemGoal(AnimalEntity mob, double speed, Predicate<ItemStack> foodPredicate, StackHandler stackHandler) {
        this(mob, speed, foodPredicate, 1.0D, stackHandler);
    }

    public TemptItemGoal(AnimalEntity mob, double speed, Predicate<ItemStack> foodPredicate, double offsetY, StackHandler stackHandler) {
        this.mob = mob;
        this.speed = speed;
        this.foodPredicate = foodPredicate;
        this.offsetY = offsetY;
        this.stackHandler = stackHandler;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    public boolean canStart() {
        if (mob.isInLove() || mob.isBaby() || mob.getBreedingAge() > 0) {
            return false;
        }

        if (cooldown > 0) {
            --cooldown;
            return false;
        }

        mob.getEntityWorld().getEntitiesByClass(ItemEntity.class, mob.getBoundingBox().expand(16.0, 4.0, 16.0), EntityPredicates.VALID_ENTITY)
                .stream().filter(entity -> foodPredicate.test(entity.getStack())).findFirst().ifPresentOrElse(entity -> itemEntity = entity, () -> itemEntity = null);

        if (itemEntity == null) {
            cooldown = COOLDOWN;
            return false;
        } else
            return true;
    }

    public boolean shouldContinue() {
        return (itemEntity != null ? itemEntity.squaredDistanceTo(mob) : 36.1) < 36.0 || canStart();
    }

    public void start() {
        active = true;
    }

    public void stop() {
        mob.getNavigation().stop();
        cooldown = COOLDOWN;
        active = false;
        itemEntity = null;
    }

    public void tick() {
        if (itemEntity == null || !itemEntity.isAlive()) {
            stop();
            return;
        }

        mob.getLookControl().lookAt(itemEntity, (mob.getMaxHeadRotation() + 20), mob.getMaxLookPitchChange());

        if (mob.squaredDistanceTo(itemEntity) < INTERSECT_DISTANCE && mob.getBoundingBox().intersects(itemEntity.getBoundingBox())) {
            if (itemEntity != null) {
                itemEntity.setStack(stackHandler.affect(itemEntity.getStack()));
                mob.lovePlayer(null);
            }
            stop();
            return;
        }

        mob.getNavigation().startMovingTo(adjust(mob.getX(), itemEntity.getX()), itemEntity.getY() + offsetY, adjust(mob.getZ(), itemEntity.getZ()), speed);
    }

    public double adjust(double mobAxis, double itemAxis) {
        return adjust(mobAxis, itemAxis, INTERSECT_DISTANCE);
    }

    public double adjust(double mobAxis, double itemAxis, double distance) {
        if (Math.abs(mobAxis - itemAxis) < 0.25D) {
            return itemAxis;
        }

        return itemAxis + (mobAxis < itemAxis ? distance : -distance);
    }
}
