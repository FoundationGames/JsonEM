package io.github.foundationgames.jsonem.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import io.github.foundationgames.jsonem.JsonEM;
import io.github.foundationgames.jsonem.serialization.JsonEMCodecs;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

public final class JsonEntityModelUtil {
    public static final Path DUMP_DIR = FabricLoader.getInstance().getGameDir().resolve("jsonem_dump");
    public static final Gson GSON = new Gson();

    private JsonEntityModelUtil() {}

    public static Optional<TexturedModelData> readJson(InputStream data) {
        JsonElement json = GSON.fromJson(GSON.newJsonReader(new InputStreamReader(data)), JsonObject.class);

        return JsonEMCodecs.TEXTURED_MODEL_DATA.decode(JsonOps.INSTANCE, json).result().map(Pair::getFirst);
    }

    public static void loadModels(ResourceManager manager, Map<EntityModelLayer, TexturedModelData> models) {
        EntityModelLayers.getLayers().forEach(layer -> {
            var modelLoc = new Identifier(layer.getId().getNamespace(), "models/entity/"+layer.getId().getPath()+"/"+layer.getName()+".json");

            if (manager.containsResource(modelLoc)) {
                try {
                    var res = manager.getResource(modelLoc);
                    try (var in = res.getInputStream()) {
                        var data = JsonEntityModelUtil.readJson(in);
                        data.ifPresent(model -> models.put(layer, model));
                    }
                } catch (IOException e) {
                    JsonEM.LOG.error(e);
                }
            }
        });
    }

    public static void dump(EntityModelLayer layer, TexturedModelData data) throws IOException {
        if (!Files.exists(DUMP_DIR)) {
            Files.createDirectories(DUMP_DIR);
        }

        var modelResult = JsonEMCodecs.TEXTURED_MODEL_DATA.encode(data, JsonOps.INSTANCE, new JsonObject());
        var modelFolder = DUMP_DIR.resolve("assets").resolve(layer.getId().getNamespace()).resolve("models").resolve("entity").resolve(layer.getId().getPath());
        var modelFile = modelFolder.resolve(layer.getName()+".json");

        if (!Files.exists(modelFolder)) {
            Files.createDirectories(modelFolder);
        }

        var element = modelResult.get().left();
        if (element.isPresent()) {
            var writer = GSON.newJsonWriter(Files.newBufferedWriter(modelFile));
            writer.setIndent("    ");
            GSON.toJson(element.get(), writer);

            writer.close();
        }
    }
}
