package me.cable.crossover.main.task;

import me.cable.crossover.main.handler.SettingsConfigHandler;
import me.cable.crossover.main.util.ConfigHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class FallTeleportTask implements Runnable {

    public static final Map<Player, Location> lastGroundLocations = new HashMap<>();

    @Override
    public void run() {
        ConfigHelper fallTeleportConfig = SettingsConfigHandler.getConfig().ch("fall-teleport");

        for (World world : Bukkit.getWorlds()) {
            String worldName = world.getName();
            if (!fallTeleportConfig.isSet(worldName)) continue;

            double tpy = fallTeleportConfig.doub(worldName);

            for (Player player : world.getPlayers()) {
                if (((Entity) player).isOnGround()) {
                    lastGroundLocations.put(player, player.getLocation());
                }

                Location loc = player.getLocation();
                if (loc.getY() > tpy) continue;

                Location tpLoc = lastGroundLocations.get(player);

                if (tpLoc == null) {
                    player.kickPlayer("You fell too far.");
                } else {
                    player.teleport(tpLoc);
                }
            }
        }
    }
}
