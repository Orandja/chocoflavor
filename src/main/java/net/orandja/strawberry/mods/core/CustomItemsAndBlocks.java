package net.orandja.strawberry.mods.core;

import net.orandja.strawberry.mods.resourcepack.StrawberryResourcePackGenerator;

public abstract class CustomItemsAndBlocks {

    public static void beforeLaunch() {
        StrawberryResourcePackGenerator.generate();
    }

}
