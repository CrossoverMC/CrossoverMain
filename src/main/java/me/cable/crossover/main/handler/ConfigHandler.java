package me.cable.crossover.main.handler;

import me.cable.crossover.main.CrossoverMain;
import me.cable.crossover.main.util.ConfigHelper;
import me.cable.crossover.main.util.YamlLoader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConfigHandler {

    public static final String PATH_INVENTORY_ITEMS = "inventory-items";
    public static final String PATH_MESSAGES = "messages";

    private final CrossoverMain crossoverMain;

    private static final Map<String, YamlConfiguration> configs = new HashMap<>();

    public ConfigHandler(@NotNull CrossoverMain crossoverMain) {
        this.crossoverMain = crossoverMain;
        load(null);
    }

    private static @NotNull ConfigHelper getConfig(@NotNull String name) {
        return new ConfigHelper(configs.get(name));
    }

    public static @NotNull ConfigHelper settings() {
        return getConfig("config.yml");
    }

    public static @NotNull ConfigHelper npcChatSettings() {
        return getConfig("npc-chat.yml");
    }

    public static @NotNull ConfigHelper shopSettings() {
        return getConfig("shops.yml");
    }

    public static @NotNull Set<String> getShopIds() {
        return shopSettings().getKeys(false);
    }

    public void load(@Nullable Player player) {
        for (String fileName : List.of("config", "npc-chat", "shops")) {
            fileName += ".yml";
            YamlConfiguration config = new YamlLoader(fileName, crossoverMain).resource(crossoverMain)
                    .logger(crossoverMain).player(player).load().config();
            configs.put(fileName, config);
        }
    }
}
