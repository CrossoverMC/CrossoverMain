package me.cable.crossover.main.handler;

import me.cable.crossover.main.CrossoverMain;
import me.cable.crossover.main.util.YamlLoader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class SettingsHandler {

    private final CrossoverMain crossoverMain;

    private final File file;

    public SettingsHandler(@NotNull CrossoverMain crossoverMain) {
        this.crossoverMain = crossoverMain;
        file = new File(crossoverMain.getDataFolder(), "config.yml");
        load(null);
    }

    public void load(@Nullable Player player) {
        YamlConfiguration config = new YamlLoader(file).resource(crossoverMain)
                .logger(crossoverMain).player(player).load().config();
        Settings.get().setCs(config);
    }
}
