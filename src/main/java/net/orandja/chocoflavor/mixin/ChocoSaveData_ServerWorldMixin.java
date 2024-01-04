package net.orandja.chocoflavor.mixin;

import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.RandomSequencesState;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.orandja.chocoflavor.ChocoSaveData;
import net.orandja.chocoflavor.utils.Settings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

@Mixin(ServerWorld.class)
public abstract class ChocoSaveData_ServerWorldMixin extends World implements ChocoSaveData.Handler {

    protected ChocoSaveData_ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
    }

    @Inject(at = @At("RETURN"),
            method = "<init>")
    private void init(MinecraftServer minecraftServer, Executor executor, LevelStorage.Session session, ServerWorldProperties serverWorldProperties, RegistryKey registryKey, DimensionOptions dimensionOptions, WorldGenerationProgressListener worldGenerationProgressListener, boolean bl, long l, List list, boolean bl2, RandomSequencesState randomSequencesState, CallbackInfo ci) {
        loadExtraSaveData(registryKey);
        Settings.save();
    }

    @Inject(method = "saveLevel", at = @At("RETURN"))
    void saveLevel(CallbackInfo info) {
        Settings.save();
        saveExtraSaveData(getRegistryKey());
    }
}
