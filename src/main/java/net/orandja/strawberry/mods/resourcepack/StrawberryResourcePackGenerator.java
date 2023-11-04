package net.orandja.strawberry.mods.resourcepack;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.orandja.chocoflavor.ChocoFlavor;
import net.orandja.chocoflavor.utils.Utils;
import net.orandja.strawberry.mods.core.NoteBlockData;
import net.orandja.strawberry.mods.core.block.StrawberryBlock;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipFile;

public abstract class StrawberryResourcePackGenerator {

    public record CustomModelData(int id, String model, String[] textures) {

    }

    public static Map<NoteBlockData, String> noteblockModels = new HashMap<>();
    public static Map<String, CustomModelData[]> itemCustomModelData = new HashMap<>();
    public static List<CustomModelData> getModelData(String name) {
        return itemModelData.computeIfAbsent(name, key -> new ArrayList<>());
    }
    public static Map<String, List<CustomModelData>> itemModelData = new HashMap<>();

    public static void writeTo(String path, JsonObject mainNode) {
        try {
            StringWriter writer = new StringWriter();
            JsonWriter jsonWriter = new JsonWriter(writer);
            jsonWriter.setIndent("  ");
            jsonWriter.setLenient(true);
            Streams.write(mainNode, jsonWriter);

            FileUtils.write(new File(path), writer.toString(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        Utils.executeAfter(StrawberryResourcePackGenerator::_save, 1000L);
    }

    public static void _save() {
        writeTo("../resourcepack/assets/minecraft/blockstates/note_block.json", Utils.create(JsonObject::new, mainNode -> {
            mainNode.add("variants", Utils.create(JsonObject::new, variants -> {

                Utils.apply(getSourceModel("item/note_block"), sourceModel -> {
                    sourceModel.add("overrides", Utils.create(JsonArray::new, overrides -> {
                        noteblockModels.keySet().stream().sorted(Comparator.comparing(NoteBlockData::toID)).forEach(data -> {
                            String modelPath = noteblockModels.get(data);
                            variants.add(data.toBlockStateString(), Utils.create(JsonObject::new, model -> {
                                model.addProperty("model", modelPath);
                            }));

                            overrides.add(Utils.create(JsonObject::new, override -> {
                                override.add("predicate", Utils.create(JsonObject::new, predicate -> {
                                    predicate.addProperty("custom_model_data", data.toID());
                                }));

                                override.addProperty("model", modelPath.replace("minecraft:block/", "minecraft:item/"));
                            }));

                            writeTo("../resourcepack/assets/minecraft/models/item/" + modelPath.replace("minecraft:block/", "") + ".json", Utils.create(JsonObject::new, customBlockItemModel -> {
                                customBlockItemModel.addProperty("parent", modelPath);
                            }));
                        });
                    }));

                    writeTo("../resourcepack/assets/minecraft/models/item/note_block.json", sourceModel);
                });
            }));
        }));

        itemModelData.forEach((path, customModels) -> {

            writeTo("../resourcepack/assets/minecraft/models/item/" + path + ".json", Utils.apply(getSourceModel("item/" + path), sourceModel -> {
                sourceModel.add("overrides", Utils.create(JsonArray::new, overrides -> {
                    customModels.stream().sorted(Comparator.comparing(CustomModelData::id)).forEach(modelData -> {
                        overrides.add(Utils.create(JsonObject::new, override -> {
                            override.add("predicate", Utils.create(JsonObject::new, predicate -> {
                                predicate.addProperty("custom_model_data", modelData.id);
                            }));
                            override.addProperty("model", "minecraft:"+ "item/" + modelData.model);
                        }));

//                        JsonObject customItemModel = new JsonObject();

                        writeTo("../resourcepack/assets/minecraft/models/item/" + modelData.model + ".json", Utils.create(JsonObject::new, customModel -> {
                            customModel.addProperty("parent", sourceModel.get("parent").getAsString());
                            customModel.add("textures", Utils.create(JsonObject::new, textures -> {
                                for (int i = 0; i < modelData.textures.length; i++) {
                                    textures.addProperty("layer"+ i, "minecraft:item/" + modelData.textures[i]);
                                }
                            }));
                        }));
                    });
                }));
            }));
//            JsonObject modelObject = getSourceModel("item/" + path);

//            writeTo("../resourcepack/assets/minecraft/models/item/" + path + ".json", modelObject);
        });

//        itemModelData.forEach((path, customModels) -> {
//            JsonObject modelObject = getSourceModel("item/" + path);
//            JsonArray overrides = new JsonArray();
//            modelObject.add("overrides", overrides);
//
//            for (CustomModelData modelData : customModels) {
//                JsonObject override = new JsonObject();
//                JsonObject predicate = new JsonObject();
//                predicate.addProperty("custom_model_data", modelData.id);
//
//                override.add("predicate", predicate);
//                override.addProperty("model", "minecraft:"+ "item/" + modelData.model);
//                overrides.add(override);
//
//                JsonObject customItemModel = new JsonObject();
//                customItemModel.addProperty("parent", modelObject.get("parent").getAsString());
//                JsonObject textures = new JsonObject();
//                for (int i = 0; i < modelData.textures.length; i++) {
//                    textures.addProperty("layer"+ i, "minecraft:item/" + modelData.textures[i]);
//                }
//                customItemModel.add("textures", textures);
//
//                writeTo("../resourcepack/assets/minecraft/models/item/" + modelData.model + ".json", customItemModel);
//            }
//
//            writeTo("../resourcepack/assets/minecraft/models/item/" + path + ".json", modelObject);
//        });
    }

    public static JsonObject getSourceModel(String path) {
        try {
            ZipFile zipFile = new ZipFile("/home/oliviermartinez/coding/choco-flavor/.gradle/loom-cache/minecraftMaven/net/minecraft/minecraft-clientOnly-9615416516/1.20.2-net.fabricmc.yarn.1_20_2.1.20.2+build.1-v2/minecraft-clientOnly-9615416516-1.20.2-net.fabricmc.yarn.1_20_2.1.20.2+build.1-v2.jar");
            return JsonParser.parseString(new BufferedReader(
                    new InputStreamReader(zipFile.getInputStream(zipFile.getEntry("assets/minecraft/models/"+ path +".json")), StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"))).getAsJsonObject();
        } catch (Exception e) {
            ChocoFlavor.LOGGER.info(path);
            e.printStackTrace();
        }

        return new JsonObject();
    }

    public static void generate() {
//        noteblockModels.put(NoteBlockData.fromID(1), "minecraft:block/rice_crop0");
//        noteblockModels.put(NoteBlockData.fromID(2), "minecraft:block/rice_crop1");
//        noteblockModels.put(NoteBlockData.fromID(3), "minecraft:block/rice_crop2");
//        noteblockModels.put(NoteBlockData.fromID(4), "minecraft:block/rice_crop3");
//
//        noteblockModels.put(NoteBlockData.fromID(5), "minecraft:block/cabbage_crop0");
//        noteblockModels.put(NoteBlockData.fromID(6), "minecraft:block/cabbage_crop1");
//        noteblockModels.put(NoteBlockData.fromID(7), "minecraft:block/cabbage_crop2");
//        noteblockModels.put(NoteBlockData.fromID(8), "minecraft:block/cabbage_crop3");
//
//        noteblockModels.put(NoteBlockData.fromID(9), "minecraft:block/onion_crop0");
//        noteblockModels.put(NoteBlockData.fromID(10), "minecraft:block/onion_crop1");
//        noteblockModels.put(NoteBlockData.fromID(11), "minecraft:block/onion_crop2");
//        noteblockModels.put(NoteBlockData.fromID(12), "minecraft:block/onion_crop3");
//
//        noteblockModels.put(NoteBlockData.fromID(13), "minecraft:block/tomato_crop0");
//        noteblockModels.put(NoteBlockData.fromID(14), "minecraft:block/tomato_crop1");
//        noteblockModels.put(NoteBlockData.fromID(15), "minecraft:block/tomato_crop2");
//        noteblockModels.put(NoteBlockData.fromID(16), "minecraft:block/tomato_crop3");

//        itemCustomModelData.put("item/baked_potato", new CustomModelData[] {
//            new CustomModelData(1, "item/cooked_rice", "minecraft:item/cooked_rice"),
//            new CustomModelData(2, "item/cabbage_leaf", "minecraft:item/cabbage_leaf"),
//            new CustomModelData(3, "item/onion", "minecraft:item/onion"),
//            new CustomModelData(4, "item/tomato", "minecraft:item/tomato")
//        });
//
//        itemCustomModelData.put("item/potato", new CustomModelData[] {
//                new CustomModelData(1, "item/rice", "minecraft:item/rice"),
//        });
//
//        itemCustomModelData.put("item/wheat", new CustomModelData[] {
//                new CustomModelData(1, "item/rice", "minecraft:item/rice"),
//                new CustomModelData(2, "item/cabbage", "minecraft:item/cabbage")
//        });
//
//        itemCustomModelData.put("item/wheat_seeds", new CustomModelData[] {
//                new CustomModelData(1, "item/rice_seeds", "minecraft:item/rice_seeds"),
//                new CustomModelData(2, "item/cabbage_seeds", "minecraft:item/cabbage_seeds"),
//                new CustomModelData(3, "item/onion_seeds", "minecraft:item/onion_seeds"),
//                new CustomModelData(4, "item/tomato_seeds", "minecraft:item/tomato_seeds")
//        });
//
//        itemCustomModelData.put("item/barrier", new CustomModelData[] {
//                new CustomModelData(10021, "item/orange_progress_0_21", "minecraft:item/progress/progress_0_21"),
//                new CustomModelData(10121, "item/orange_progress_1_21", "minecraft:item/progress/progress_1_21"),
//                new CustomModelData(10221, "item/orange_progress_2_21", "minecraft:item/progress/progress_2_21"),
//                new CustomModelData(10321, "item/orange_progress_3_21", "minecraft:item/progress/progress_3_21"),
//                new CustomModelData(10421, "item/orange_progress_4_21", "minecraft:item/progress/progress_4_21"),
//                new CustomModelData(10521, "item/orange_progress_5_21", "minecraft:item/progress/progress_5_21"),
//                new CustomModelData(10621, "item/orange_progress_6_21", "minecraft:item/progress/progress_6_21"),
//                new CustomModelData(10721, "item/orange_progress_7_21", "minecraft:item/progress/progress_7_21"),
//                new CustomModelData(10821, "item/orange_progress_8_21", "minecraft:item/progress/progress_8_21"),
//                new CustomModelData(10921, "item/orange_progress_9_21", "minecraft:item/progress/progress_9_21"),
//                new CustomModelData(11021, "item/orange_progress_10_21", "minecraft:item/progress/progress_10_21"),
//                new CustomModelData(11121, "item/orange_progress_11_21", "minecraft:item/progress/progress_11_21"),
//                new CustomModelData(11221, "item/orange_progress_12_21", "minecraft:item/progress/progress_12_21"),
//                new CustomModelData(11321, "item/orange_progress_13_21", "minecraft:item/progress/progress_13_21"),
//                new CustomModelData(11421, "item/orange_progress_14_21", "minecraft:item/progress/progress_14_21"),
//                new CustomModelData(11521, "item/orange_progress_15_21", "minecraft:item/progress/progress_15_21"),
//                new CustomModelData(11621, "item/orange_progress_16_21", "minecraft:item/progress/progress_16_21"),
//                new CustomModelData(11721, "item/orange_progress_17_21", "minecraft:item/progress/progress_17_21"),
//                new CustomModelData(11821, "item/orange_progress_18_21", "minecraft:item/progress/progress_18_21"),
//                new CustomModelData(11921, "item/orange_progress_19_21", "minecraft:item/progress/progress_19_21"),
//                new CustomModelData(12021, "item/orange_progress_20_21", "minecraft:item/progress/progress_20_21"),
//                new CustomModelData(12121, "item/orange_progress_21_21", "minecraft:item/progress/progress_21_21")
//        });
//        save();
    }
}
