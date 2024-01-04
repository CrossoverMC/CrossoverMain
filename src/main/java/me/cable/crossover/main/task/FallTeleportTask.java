package me.cable.crossover.main.task;

import me.cable.crossover.main.handler.SettingsConfigHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class FallTeleportTask implements Runnable {

    @Override
    public void run() {
        if (!SettingsConfigHandler.getConfig().fallTeleport_enabled()) return;

        int yFrom = SettingsConfigHandler.getConfig().fallTeleport_yFrom();
        int yTo = SettingsConfigHandler.getConfig().fallTeleport_yTo();

        for (Player player : Bukkit.getOnlinePlayers()) {
            Location loc = player.getLocation();

            if (loc.getY() <= yFrom) {
                loc.setY(yTo);
                player.teleport(loc);
            }
        }
    }
}
