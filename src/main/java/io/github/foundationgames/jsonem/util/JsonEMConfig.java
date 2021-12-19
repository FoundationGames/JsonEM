package io.github.foundationgames.jsonem.util;

import io.github.foundationgames.jsonem.JsonEM;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class JsonEMConfig {
    public static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("jsonem.properties");

    public final Properties values = new Properties();

    private static void defaultValues(Properties values) {
        values.setProperty("dump_models", "false");
    }

    public void load() {
        try {
            if (!Files.exists(FILE)) {
                save();
            }

            this.values.clear();
            try (var in = Files.newInputStream(FILE)) {
                this.values.load(in);
            }
        } catch (IOException ex) {
            JsonEM.LOG.error("Error loading config file for Json Entity Models", ex);
        }
    }

    public void save() {
        try {
            if (!Files.exists(FILE)) {
                Files.createFile(FILE);
            }

            defaultValues(this.values);
            try (var out = Files.newOutputStream(FILE)) {
                this.values.store(out, null);
            }
        } catch (IOException ex) {
            JsonEM.LOG.error("Error saving config file for Json Entity Models", ex);
        }
    }
}
