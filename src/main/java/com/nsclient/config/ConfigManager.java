package com.nsclient.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Loads / saves {@link NSConfig} as pretty JSON at config/nsclient/config.json.
 * Falls back to defaults (and re-creates the file) if the config is missing or unreadable.
 */
public final class ConfigManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static NSConfig config = new NSConfig();

    private ConfigManager() {
    }

    public static NSConfig get() {
        return config;
    }

    private static Path configDir() {
        return FabricLoader.getInstance().getConfigDir().resolve("nsclient");
    }

    private static Path configFile() {
        return configDir().resolve("config.json");
    }

    public static void load() {
        Path file = configFile();
        if (!Files.exists(file)) {
            save();
            return;
        }
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            NSConfig loaded = GSON.fromJson(reader, NSConfig.class);
            if (loaded != null) {
                config = loaded;
            }
        } catch (IOException | com.google.gson.JsonSyntaxException e) {
            // Corrupt or unreadable config: keep defaults in memory, overwrite the file on next save.
            config = new NSConfig();
        }
    }

    public static void save() {
        try {
            Path dir = configDir();
            Files.createDirectories(dir);
            try (Writer writer = Files.newBufferedWriter(configFile(), StandardCharsets.UTF_8)) {
                GSON.toJson(config, writer);
            }
        } catch (IOException e) {
            // Best-effort: if we can't persist, the running session still keeps the in-memory values.
        }
    }
}
