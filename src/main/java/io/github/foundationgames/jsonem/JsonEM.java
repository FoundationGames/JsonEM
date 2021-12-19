package io.github.foundationgames.jsonem;

import io.github.foundationgames.jsonem.util.JsonEMConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JsonEM implements ClientModInitializer {
    public static final Logger LOG = LogManager.getLogger("Json Entity Models");
    public static final JsonEMConfig CONFIG = new JsonEMConfig();

    @Override
    public void onInitializeClient() {
        CONFIG.load();
    }

    public static void registerModelLayer(EntityModelLayer layer) {
        EntityModelLayerRegistry.registerModelLayer(layer, () -> TexturedModelData.of(new ModelData(), 32, 32));
    }
}
