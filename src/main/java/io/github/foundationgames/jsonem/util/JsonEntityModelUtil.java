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
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

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
        ResourceFinder.json("models/entity").findResources(manager).forEach((id, res) -> {
            try {
                var fullPath = id.getPath().replaceFirst("models/entity/", "");
                var splitPath = fullPath.split("/");

                var dirs = new String[splitPath.length - 1];
                System.arraycopy(splitPath, 0, dirs, 0, dirs.length);

                var layerName = splitPath[splitPath.length - 1].replace(".json", "");
                var modelName = String.join("/", dirs);

                var layer = new EntityModelLayer(Identifier.of(id.getNamespace(), modelName), layerName);

                try (var in = res.getInputStream()) {
                    var data = JsonEntityModelUtil.readJson(in);
                    data.ifPresent(model -> models.put(layer, model));
                }
            } catch (IOException | InvalidIdentifierException e) {
                JsonEM.LOG.error(e);
            }
        });
    }

    public static void dump(EntityModelLayer layer, TexturedModelData data) throws IOException {
        if (!Files.exists(DUMP_DIR)) {
            Files.createDirectories(DUMP_DIR);
        }

        var modelResult = JsonEMCodecs.TEXTURED_MODEL_DATA.encode(data, JsonOps.INSTANCE, new JsonObject());
        var modelFolder = DUMP_DIR.resolve("assets").resolve(layer.id().getNamespace()).resolve("models").resolve("entity").resolve(layer.id().getPath());
        var modelFile = modelFolder.resolve(layer.name()+".json");

        if (!Files.exists(modelFolder)) {
            Files.createDirectories(modelFolder);
        }

        if (modelResult.isSuccess()) {
            var element = modelResult.getOrThrow();
            var writer = GSON.newJsonWriter(Files.newBufferedWriter(modelFile));
            writer.setIndent("    ");
            GSON.toJson(element, writer);

            writer.close();
        }
    }
}
