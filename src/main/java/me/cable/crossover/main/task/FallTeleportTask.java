package me.cable.crossover.main.task;

import me.cable.crossover.main.handler.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class FallTeleportTask implements Runnable {

    @Override
    public void run() {
        if (!Settings.get().fallTeleport_enabled()) return;

        int yFrom = Settings.get().fallTeleport_yFrom();
        int yTo = Settings.get().fallTeleport_yTo();

        for (Player player : Bukkit.getOnlinePlayers()) {
            Location loc = player.getLocation();

            if (loc.getY() <= yFrom) {
                loc.setY(yTo);
                player.teleport(loc);
            }
        }
    }
}
