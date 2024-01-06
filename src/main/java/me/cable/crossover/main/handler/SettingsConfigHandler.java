package me.cable.crossover.main.handler;

import me.cable.crossover.main.CrossoverMain;
import me.cable.crossover.main.util.ConfigHelper;
import me.cable.crossover.main.util.YamlLoader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class SettingsConfigHandler {

    public static final String PATH_PLAYER_ITEMS = "player-items";

    private final CrossoverMain crossoverMain;

    private static YamlConfiguration config;
    private final File file;

    public SettingsConfigHandler(@NotNull CrossoverMain crossoverMain) {
        this.crossoverMain = crossoverMain;
        file = new File(crossoverMain.getDataFolder(), "config.yml");
        load(null);
    }

    public static @NotNull ConfigHelper getConfig() {
        return new ConfigHelper(config);
    }

    public void load(@Nullable Player player) {
        config = new YamlLoader(file).resource(crossoverMain)
                .logger(crossoverMain).player(player).load().config();
    }
}
