package me.cable.crossover.main.handler;

import me.cable.crossover.main.CrossoverMain;
import me.cable.crossover.main.util.YamlLoader;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public class PlayerData {

    /* Keys
        currencies
        egypt-artifacts
        items
        pets
        speed-boost
     */

    public static final String PATH_SPEED_BOOST = "speed-boost";

    public static final int UNLOAD_TIME = 60; // seconds
    private static PlayerData instance;

    private final CrossoverMain crossoverMain;

    private final Map<UUID, LoadedPlayerData> loadedPlayerData = new HashMap<>();
    private final File playerDataDirectory;

    public PlayerData(@NotNull CrossoverMain crossoverMain) {
        if (instance != null) {
            throw new IllegalStateException(getClass().getSimpleName() + " has already been instantiated");
        }

        instance = this;
        this.crossoverMain = crossoverMain;
        playerDataDirectory = new File(crossoverMain.getDataFolder(), "playerdata");
        startUnloader();
    }

    public static @NotNull YamlConfiguration get(@NotNull UUID playerUuid) {
        return instance.getInternal(playerUuid);
    }

    private void startUnloader() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(crossoverMain, () -> {
            for (Entry<UUID, LoadedPlayerData> entry : Map.copyOf(loadedPlayerData).entrySet()) {
                UUID playerUuid = entry.getKey();
                LoadedPlayerData playerData = entry.getValue();

                if (Bukkit.getPlayer(playerUuid) != null) {
                    playerData.unloadTime = UNLOAD_TIME;
                } else if (--playerData.unloadTime <= 0) {
                    saveRaw(playerUuid, playerData.config);
                    loadedPlayerData.remove(playerUuid);
                }
            }
        }, 0, 20);
    }

    private void log(@NotNull String message) {
        crossoverMain.getLogger().info(message);
    }

    private @NotNull File getPlayerFile(@NotNull UUID playerUuid) {
        return new File(playerDataDirectory, playerUuid + ".yml");
    }

    private @NotNull LoadedPlayerData load(@NotNull UUID playerUuid) {
        log("Loading player data: " + playerUuid);
        LoadedPlayerData playerData = new LoadedPlayerData();
        playerData.config = new YamlLoader(getPlayerFile(playerUuid)).logger(CrossoverMain.getInstance()).load().config();
        playerData.unloadTime = UNLOAD_TIME;
        return playerData;
    }

    private @NotNull YamlConfiguration getInternal(@NotNull UUID playerUuid) {
        return loadedPlayerData.computeIfAbsent(playerUuid, this::load).config;
    }

    private void saveRaw(@NotNull UUID playerUuid, @NotNull YamlConfiguration playerData) {
        File file = getPlayerFile(playerUuid);
        log("Unloading player data: " + playerUuid);

        try {
            playerData.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveAll() {
        for (Entry<UUID, LoadedPlayerData> entry : loadedPlayerData.entrySet()) {
            saveRaw(entry.getKey(), entry.getValue().config);
        }

        loadedPlayerData.clear();
    }

    private static class LoadedPlayerData {
        YamlConfiguration config;
        int unloadTime;
    }
}
