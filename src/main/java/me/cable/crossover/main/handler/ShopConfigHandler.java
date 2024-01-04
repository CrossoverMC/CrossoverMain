package me.cable.crossover.main.handler;

import me.cable.crossover.main.CrossoverMain;
import me.cable.crossover.main.util.ConfigHelper;
import me.cable.crossover.main.util.YamlLoader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ShopConfigHandler {

    private final CrossoverMain crossoverMain;

    private static YamlConfiguration config;
    private final File file;

    public ShopConfigHandler(@NotNull CrossoverMain crossoverMain) {
        this.crossoverMain = crossoverMain;
        file = new File(crossoverMain.getDataFolder(), "shops.yml");
        load(null);
    }

    public static @NotNull ConfigHelper getConfig() {
        return new ConfigHelper(config);
    }

    public static @NotNull List<String> getShopIds() {
        return new ArrayList<>(getConfig().getKeys(false));
    }

    public void load(@Nullable Player player) {
        config = new YamlLoader(file).resource(crossoverMain)
                .logger(crossoverMain).player(player).load().config();
    }
}
