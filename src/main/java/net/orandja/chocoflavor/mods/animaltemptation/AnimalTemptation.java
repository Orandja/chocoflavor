package net.orandja.chocoflavor.mods.animaltemptation;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.world.World;
import net.orandja.chocoflavor.ChocoFlavor;

import java.util.Map;
import java.util.function.Consumer;

public interface AnimalTemptation {

    interface AnimalItemGoal {
        Pair<Integer, TemptItemGoal> getGoal(AnimalEntity entity);
    }

    static void addGoal(GoalSelector selector, Pair<Integer, TemptItemGoal> pair) {
        selector.add(pair.getLeft(), pair.getRight());
    }

    Map<Class<? extends AnimalEntity>, AnimalItemGoal> TEMPTATIONS = Maps.newHashMap();

    static void getTemptation(AnimalEntity animal, Consumer<AnimalItemGoal> consumer) {
        TEMPTATIONS.keySet().stream().filter(clazz -> animal.getClass().isAssignableFrom(clazz) || animal.getClass() == clazz)
                .findFirst().ifPresent(animalEntityClass -> consumer.accept(TEMPTATIONS.get(animalEntityClass)));
    }

    static void beforeLaunch() {
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
    }

    GoalSelector vw$_getGoalSelector();

    default void applyTo(World world) {
        if(world != null && !world.isClient && this instanceof AnimalEntity animal) {
            getTemptation(animal, goalPair -> addGoal(this.vw$_getGoalSelector(), goalPair.getGoal(animal)));
        }
    }
}
