package net.orandja.chocoflavor;

import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.orandja.chocoflavor.utils.MathUtils;

public class ChocoSpawners {
    private ChocoSpawners() {}

    public interface Handler {
        int getSpawnDelay();
        int setSpawnDelay(int delay);

        int getRequiredPlayerRange();

        int getDelayReduction();
        void setDelayReduction(int delay);

        int getNextLoad();
        void setNextLoad(int load);

        Box BOX = new Box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);

        default void tickBetterSpawner(ServerWorld world, BlockPos pos) {
            double requiredDistance = (getRequiredPlayerRange() * getRequiredPlayerRange());
            if(getSpawnDelay() <= getNextLoad()) {
                // 100⌊x/100⌋ to eliminate double digits ticks... I don't remember why; should have commented earlier. LUL
                setNextLoad((int) (Math.floor(getSpawnDelay() / 100.0D) * 100));
                long playerCount = world.getPlayers(EntityPredicates.EXCEPT_SPECTATOR).stream().filter(EntityPredicates.VALID_LIVING_ENTITY).filter(MathUtils.inRange(pos, requiredDistance)).count();

                // for debugging when alone.
//            Box box = BOX.offset(pos).expand(10, 10, 10);
//            List<ArmorStandEntity> armorstands = world.getNonSpectatingEntities(ArmorStandEntity.class, box);
//            playerCount += armorstands.size();
                setDelayReduction((int) Math.max(1, Math.pow(3, (playerCount - 1))));
            }

            if((setSpawnDelay(Math.max(0, getSpawnDelay() - getDelayReduction()))) <= 0) {
                setNextLoad(800);
            }
        }
    }
}
