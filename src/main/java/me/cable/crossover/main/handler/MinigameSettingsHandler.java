package me.cable.crossover.main.handler;

import me.cable.crossover.main.CrossoverMain;
import me.cable.crossover.main.features.highblock.HighblockMinigame;
import me.cable.crossover.main.features.spleef.SpleefMinigame;
import me.cable.crossover.main.object.Minigame;
import me.cable.crossover.main.util.ConfigHelper;
import me.cable.crossover.main.util.YamlLoader;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

public class MinigameSettingsHandler {

    public static final String MINIGAMES_PATH = "minigames";

    private static final ConfigHelper configHelper = new ConfigHelper();

    private final CrossoverMain crossoverMain;

    private final File file;

    public static @NotNull ConfigHelper get() {
        return configHelper;
    }

    public MinigameSettingsHandler(@NotNull CrossoverMain crossoverMain) {
        this.crossoverMain = crossoverMain;
        file = new File(crossoverMain.getDataFolder(), "minigames.yml");
        load(null);
    }

    public void load(@Nullable Player player) {
        Minigame.unregisterAll();
        YamlConfiguration config = new YamlLoader(file).resource(crossoverMain).logger(crossoverMain).player(player).load().config();
        configHelper.setCs(config);

        Map<String, Function<ConfigurationSection, Minigame>> map = Map.of(
                "highblock", HighblockMinigame::new,
                "spleef", SpleefMinigame::new
        );

        for (Entry<String, Function<ConfigurationSection, Minigame>> entry : map.entrySet()) {
            String minigameId = entry.getKey();
            Function<ConfigurationSection, Minigame> factory = entry.getValue();
            ConfigHelper instancesCs = configHelper.ch(MINIGAMES_PATH + "." + minigameId + ".instances");

            for (String key : instancesCs.getKeys(false)) {
                ConfigurationSection instanceCs = instancesCs.csnn(key);
                Minigame minigame = factory.apply(instanceCs);
                Minigame.register(minigame);
            }
        }
    }
}
