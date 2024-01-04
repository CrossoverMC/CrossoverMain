package me.cable.crossover.main.handler;

import me.cable.crossover.main.CrossoverMain;
import me.cable.crossover.main.util.YamlLoader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class SettingsConfigHandler {

    private final CrossoverMain crossoverMain;

    private static YamlConfiguration config;
    private final File file;

    public SettingsConfigHandler(@NotNull CrossoverMain crossoverMain) {
        this.crossoverMain = crossoverMain;
        file = new File(crossoverMain.getDataFolder(), "config.yml");
        load(null);
    }

    public static @NotNull Settings getConfig() {
        return new Settings(config);
    }

    public void load(@Nullable Player player) {
        config = new YamlLoader(file).resource(crossoverMain)
                .logger(crossoverMain).player(player).load().config();
    }
}
