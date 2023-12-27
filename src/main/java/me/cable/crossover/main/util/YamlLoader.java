package me.cable.crossover.main.util;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Logger;

public class YamlLoader {

    private final File file;

    // logging
    private @Nullable Logger logger;
    private @Nullable Player player;

    // resource
    private @Nullable String resource;
    private @Nullable JavaPlugin resourcePlugin;

    private @Nullable YamlConfiguration yamlConfiguration;
    private boolean success;

    public YamlLoader(@NotNull File file) {
        this.file = file;
    }

    public YamlLoader(@NotNull String fileName, @NotNull JavaPlugin resourcePlugin) {
        this(new File(resourcePlugin.getDataFolder(), fileName));
    }

    private void error(@NotNull String message) {
        message = message.replace("%file%", "\"" + file.getName() + "\"");

        if (logger != null) {
            logger.warning(message);
        }
        if (player != null) {
            player.sendMessage(Color.ERROR + message);
        }
    }

    public @NotNull YamlLoader logging(@Nullable JavaPlugin javaPlugin, @Nullable Player player) {
        logger(javaPlugin);
        this.player = player;
        return this;
    }

    public @NotNull YamlLoader logger(@Nullable JavaPlugin javaPlugin) {
        this.logger = (javaPlugin == null) ? null : javaPlugin.getLogger();
        return this;
    }

    public @NotNull YamlLoader player(@Nullable Player player) {
        this.player = player;
        return this;
    }

    public @NotNull YamlLoader resource(@NotNull String filePath, @NotNull JavaPlugin javaPlugin) {
        this.resource = filePath;
        this.resourcePlugin = javaPlugin;
        return this;
    }

    public @NotNull YamlLoader resource(@NotNull JavaPlugin javaPlugin) {
        return resource(file.getName(), javaPlugin);
    }

    public @NotNull YamlConfiguration config() {
        if (yamlConfiguration == null) {
            throw new IllegalStateException(
                    "This " + getClass().getSimpleName() + " has not loaded a " + YamlConfiguration.class.getSimpleName());
        }

        return yamlConfiguration;
    }

    public boolean success() {
        if (yamlConfiguration == null) {
            throw new IllegalStateException(
                    "This " + getClass().getSimpleName() + " has not loaded a " + YamlConfiguration.class.getSimpleName());
        }

        return success;
    }

    public @NotNull File file() {
        return file;
    }

    public @NotNull YamlLoader load() {
        if (yamlConfiguration != null) { // has loaded
            throw new IllegalStateException(
                    "This " + getClass().getSimpleName() + " has already loaded a " + YamlConfiguration.class.getSimpleName());
        }

        loadInternal();
        return this;
    }

    private void loadInternal() {
        yamlConfiguration = new YamlConfiguration();

        /* Create */

        if (!file.exists()) {

            /* Parent */

            File parent = file.getParentFile();

            if (!parent.exists() && !parent.mkdirs()) {
                error("Could not create the necessary directories for the file %file%");
                return;
            }

            /* File */

            if (resource == null || resourcePlugin == null) {

                /* Create File */

                try {
                    if (!file.createNewFile()) {
                        error("Attempted to create file %file% but it already existed");
                        return;
                    }
                } catch (IOException e) {
                    error("Could not create file %file%");
                    e.printStackTrace();
                    return;
                }
            } else {

                /* Load Resource */

                resourcePlugin.saveResource(resource, false);
                File resourceFile = new File(resourcePlugin.getDataFolder(), resource);

                if (!resourceFile.equals(file)) {

                    /* Move */

                    try {
                        Files.move(resourceFile.toPath(), file.toPath());
                    } catch (IOException e) {
                        error("Could not move created resource \"" + resourceFile + "\" to desired destination");
                        e.printStackTrace();
                        return;
                    }
                }
            }
        } else if (file.isDirectory()) {
            error("Could not load file %file% because it is a directory");
            return;
        }

        /* Load */

        try {
            yamlConfiguration.load(file);
            success = true;
        } catch (IOException e) {
            error("Could not load file %file%");
        } catch (InvalidConfigurationException e) {
            error("Invalid YAML formatting in %file%");
        }
    }
}
