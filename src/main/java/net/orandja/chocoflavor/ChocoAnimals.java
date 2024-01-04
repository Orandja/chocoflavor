package net.orandja.chocoflavor;

import com.google.common.collect.Maps;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Pair;
import net.minecraft.world.World;
import net.orandja.chocoflavor.entity.goal.TemptItemGoal;
import net.orandja.chocoflavor.utils.GlobalUtils;

import java.util.Map;

public class ChocoAnimals {
    private ChocoAnimals() {}

    public static void init() {
        TEMPTATIONS.put(AxolotlEntity.class, it -> new Pair<>(3, new TemptItemGoal(it, 0.33, ((AxolotlEntity)it)::isBreedingItem, 0.0, (stack) -> new ItemStack(Items.WATER_BUCKET))));
        TEMPTATIONS.put(BeeEntity.class, it -> new Pair<>(3, new TemptItemGoal(it, 1.25, ((BeeEntity)it)::isBreedingItem, 0.0)));
        TEMPTATIONS.put(CatEntity.class, it -> new Pair<>(10, new TemptItemGoal(it, 1.25, ((CatEntity)it)::isBreedingItem)));
        TEMPTATIONS.put(ChickenEntity.class, it -> new Pair<>(10, new TemptItemGoal(it, 1.25, ((ChickenEntity)it)::isBreedingItem)));
        TEMPTATIONS.put(CowEntity.class, it -> new Pair<>(3, new TemptItemGoal(it, 1.25, ((CowEntity)it)::isBreedingItem)));
        TEMPTATIONS.put(FoxEntity.class, it -> new Pair<>(3, new TemptItemGoal(it, 1.25, ((FoxEntity)it)::isBreedingItem)));
        TEMPTATIONS.put(AbstractHorseEntity.class, it -> new Pair<>(4, new TemptItemGoal(it, 1.25, ((AbstractHorseEntity)it)::isBreedingItem)));
        TEMPTATIONS.put(LlamaEntity.class, it -> new Pair<>(4, new TemptItemGoal(it, 1.25, ((LlamaEntity)it)::isBreedingItem)));
        TEMPTATIONS.put(OcelotEntity.class, it -> new Pair<>(3, new TemptItemGoal(it, 1.25, ((OcelotEntity)it)::isBreedingItem)));
        TEMPTATIONS.put(PigEntity.class, it -> new Pair<>(4, new TemptItemGoal(it, 1.25, ((PigEntity)it)::isBreedingItem)));
        TEMPTATIONS.put(RabbitEntity.class, it -> new Pair<>(3, new TemptItemGoal(it, 1.25, ((RabbitEntity)it)::isBreedingItem)));
        TEMPTATIONS.put(SheepEntity.class, it -> new Pair<>(3, new TemptItemGoal(it, 1.25, ((SheepEntity)it)::isBreedingItem)));
        TEMPTATIONS.put(WolfEntity.class, it -> new Pair<>(9, new TemptItemGoal(it, 1.25, ((WolfEntity)it)::isBreedingItem)));
        TEMPTATIONS.put(TurtleEntity.class, it -> new Pair<>(2, new TemptItemGoal(it, 1.25, ((TurtleEntity)it)::isBreedingItem)));

        TEMPTATIONS.put(StriderEntity.class, it -> new Pair<>(3, new TemptItemGoal(it, 1.25, ((StriderEntity)it)::isBreedingItem)));
        TEMPTATIONS.put(GoatEntity.class, it -> new Pair<>(3, new TemptItemGoal(it, 1.25, ((GoatEntity)it)::isBreedingItem)));
        TEMPTATIONS.put(HoglinEntity.class, it -> new Pair<>(3, new TemptItemGoal(it, 1.25, ((HoglinEntity)it)::isBreedingItem)));
        TEMPTATIONS.put(SnifferEntity.class, it -> new Pair<>(3, new TemptItemGoal(it, 1.25, ((SnifferEntity)it)::isBreedingItem)));
    }

    private static final Map<Class<? extends AnimalEntity>, GlobalUtils.PairSupplier<Integer, TemptItemGoal, AnimalEntity>> TEMPTATIONS = Maps.newHashMap();

    private static void addGoalTo(Handler handler, Pair<Integer, TemptItemGoal> pair) {
        handler.getSelector().add(pair.getLeft(), pair.getRight());
    }

    private static void applyGoalTo(Handler handler, World world) {
        if(world != null && !world.isClient && handler instanceof AnimalEntity animal) {
            TEMPTATIONS.keySet().stream().filter(GlobalUtils.isSubClass(animal.getClass())).findFirst().ifPresent(clazz -> {
                addGoalTo(handler, TEMPTATIONS.get(clazz).get(animal));
            });
        }
    }

    public interface Handler {

        GoalSelector getSelector();

        default void applyTo(World world) {
            applyGoalTo(this, world);
        }

    }

}
