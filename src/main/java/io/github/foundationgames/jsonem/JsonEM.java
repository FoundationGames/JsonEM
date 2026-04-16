package io.github.foundationgames.jsonem;

import io.github.foundationgames.jsonem.util.JsonEMConfig;
import net.fabricmc.api.ClientModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JsonEM implements ClientModInitializer {
    public static final Logger LOG = LogManager.getLogger("Json Entity Models");
    public static final JsonEMConfig CONFIG = new JsonEMConfig();

    @Override
    public void onInitializeClient() {
        CONFIG.load();
    }
}
