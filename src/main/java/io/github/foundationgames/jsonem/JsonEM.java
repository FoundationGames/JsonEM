package io.github.foundationgames.jsonem;

import net.fabricmc.api.ClientModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JsonEM implements ClientModInitializer {
    public static final Logger LOG = LogManager.getLogger("Json Entity Models");

    public static boolean DUMP_MODELS = true;

    @Override
    public void onInitializeClient() {
    }
}
