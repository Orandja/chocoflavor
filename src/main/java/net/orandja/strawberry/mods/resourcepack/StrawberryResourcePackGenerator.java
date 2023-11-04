package net.orandja.strawberry.mods.resourcepack;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;
import net.orandja.chocoflavor.ChocoFlavor;
import net.orandja.chocoflavor.utils.Utils;
import net.orandja.strawberry.mods.core.NoteBlockData;
import net.orandja.strawberry.mods.core.TripWireBlockData;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipFile;

public abstract class StrawberryResourcePackGenerator {

    public record CustomModelData(int id, String model, String[] textures) {

    }

    public static Map<NoteBlockData, String> noteblockModels = new HashMap<>();
    public static Map<TripWireBlockData, String> tripWireModels = new HashMap<>();
    public static Map<String, CustomModelData[]> itemCustomModelData = new HashMap<>();
    public static List<CustomModelData> getModelData(String name) {
        return itemModelData.computeIfAbsent(name, key -> new ArrayList<>());
    }
    public static Map<String, List<CustomModelData>> itemModelData = new HashMap<>();

    public static void writeTo(String path, JsonObject mainNode) {
            Utils.tryOrIgnore(StringWriter::new, writer -> {
                JsonWriter jsonWriter = Utils.apply(new JsonWriter(writer), it -> {
                    it.setIndent("  ");
                    it.setLenient(true);
                });
                Streams.write(mainNode, jsonWriter);

                FileUtils.write(new File(path), writer.toString(), StandardCharsets.UTF_8);
            });
    }

    public static void save() {
//        Utils.executeAfter(StrawberryResourcePackGenerator::_save, 1000L);
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

        writeTo("../resourcepack/assets/minecraft/blockstates/tripwire.json", Utils.create(JsonObject::new, mainNode -> {
            mainNode.add("variants", Utils.create(JsonObject::new, variants -> {

                Utils.apply(getSourceModel("item/string"), sourceModel -> {
                    sourceModel.add("overrides", Utils.create(JsonArray::new, overrides -> {
                        tripWireModels.keySet().stream().sorted(Comparator.comparing(TripWireBlockData::toID)).forEach(data -> {
                            String modelPath = tripWireModels.get(data);
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

                    writeTo("../resourcepack/assets/minecraft/models/item/string.json", sourceModel);
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
        });
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
}
