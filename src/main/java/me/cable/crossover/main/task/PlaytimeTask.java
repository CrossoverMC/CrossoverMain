package me.cable.crossover.main.task;

import me.cable.crossover.main.handler.LeaderboardsConfigHandler;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class PlaytimeTask implements Runnable {

    @Override
    public void run() {
        YamlConfiguration config = LeaderboardsConfigHandler.config();

        for (Player player : Bukkit.getOnlinePlayers()) {
            String path = LeaderboardsConfigHandler.PATH_PLAYTIME + "." + player.getUniqueId();
            int current = config.getInt(path);
            config.set(path, current + 1);
        }
    }
}
