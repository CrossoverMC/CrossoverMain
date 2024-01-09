package me.cable.crossover.main.handler;

import me.cable.crossover.main.CrossoverMain;
import me.cable.crossover.main.util.YamlLoader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class LeaderboardsConfigHandler {

    public static final String PATH_PLAYTIME = "playtime";

    private static YamlConfiguration config;
    private final File file;

    public LeaderboardsConfigHandler(@NotNull CrossoverMain crossoverMain) {
        file = new File(crossoverMain.getDataFolder(), "leaderboards.yml");
        config = new YamlLoader(file).logger(crossoverMain).load().config();
    }

    public static @NotNull YamlConfiguration config() {
        if (config == null) {
            throw new IllegalStateException(LeaderboardsConfigHandler.class.getSimpleName() + " has not been instantiated");
        }

        return config;
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
