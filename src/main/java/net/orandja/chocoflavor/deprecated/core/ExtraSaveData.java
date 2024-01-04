//package net.orandja.chocoflavor.mods.core;
//
//import com.google.common.collect.Maps;
//import net.minecraft.SharedConstants;
//import net.minecraft.nbt.NbtCompound;
//import net.minecraft.nbt.NbtIo;
//import net.minecraft.registry.RegistryKey;
//import net.minecraft.world.World;
//import net.orandja.chocoflavor.utils.NBTUtils;
//import net.orandja.chocoflavor.utils.Settings;
//
//import java.io.*;
//import java.util.Map;
//import java.util.concurrent.atomic.AtomicReference;
//import java.util.function.Consumer;
//import java.util.function.Supplier;
//
//public interface ExtraSaveData {
//
//    private static void readNbt() {
//        if(file.exists()) {
//            PushbackInputStream PBStream = null;
//            DataInputStream DIStream = null;
//            try {
//                PBStream = new PushbackInputStream(new FileInputStream(file), 2);
//                DIStream = new DataInputStream(PBStream);
//                loadedCompound.set(NbtIo.readCompressed(PBStream));
//            } catch(Exception e) {
//                e.printStackTrace();
//                loadedCompound.set(new NbtCompound());
//            } finally {
//                try {
//                    if(PBStream != null) {
//                        PBStream.close();
//                    }
//                    if(DIStream != null) {
//                        DIStream.close();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        } else {
//            loadedCompound.set(new NbtCompound());
//        }
//    }
//
//    static void onLoad(String name, Consumer<NbtCompound> consumer) {
//        LOAD.put(name, consumer);
//        if(loadedCompound.get() != null) {
//            NbtCompound dataCompound = loadedCompound.get().getCompound("data");
//            if(dataCompound.contains(name)) {
//                consumer.accept(dataCompound.getCompound(name));
//            }
//        }
//    }
//
//    static void onSave(String name, Supplier<NbtCompound> supplier) {
//        SAVE.put(name, supplier);
//    }
//
//    Map<String, Consumer<NbtCompound>> LOAD = Maps.newHashMap();
//    Map<String, Supplier<NbtCompound>> SAVE = Maps.newHashMap();
//    File file = new File("config/"+ Settings.FILE_NAME +"-extradata.dat");
//
//    AtomicReference<NbtCompound> loadedCompound = new AtomicReference<>(null);
//
//    default void loadExtraSaveData(RegistryKey<World> registryKey) {
//        if(registryKey != World.OVERWORLD) {
//            return;
//        }
//
//        if(loadedCompound.get() == null) {
//            readNbt();
//        }
//        NbtCompound dataCompound = loadedCompound.get().getCompound("data");
//        LOAD.forEach((name, consumer) -> {
//            if(dataCompound.contains(name))
//                consumer.accept(dataCompound.getCompound(name));
//        });
//    }
//
//    default void saveExtraSaveData(RegistryKey<World> registryKey) {
//        if(registryKey != World.OVERWORLD) {
//            return;
//        }
//
//        NbtCompound mainCompound = new NbtCompound();
//        mainCompound.put("data", NBTUtils.createBlankCompound(it -> {
//            SAVE.forEach((name, supplier) -> it.put(name, supplier.get()));
//        }));
//        mainCompound.putInt("DataVersion", SharedConstants.getGameVersion().getSaveVersion().getId());
//
//        try {
//            NbtIo.writeCompressed(mainCompound, file);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        loadedCompound.set(mainCompound);
//    }
//}
