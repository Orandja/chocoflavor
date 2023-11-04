package net.orandja.chocoflavor;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.orandja.chocoflavor.mods.core.CauldronBucketInteraction;
import net.orandja.chocoflavor.utils.Settings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicReference;

public class ChocoFlavor implements ModInitializer {

    public static final Logger LOGGER = LogManager.getLogger("chocoflavor");
    public static AtomicReference<MinecraftServer> serverReference = new AtomicReference<>(null);

    @Override
    public void onInitialize() {
            Settings.load("chocoflavor");

            beforeLaunch();
            ServerLifecycleEvents.SERVER_STARTED.register(server -> {
                serverReference.set(server);
            });
    }

    public void beforeLaunch() {
        CauldronBucketInteraction.beforeLaunch();
    }
}