package me.cable.crossover.main.task;

import me.cable.crossover.main.handler.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class VelocityBlocksTask implements Runnable {

    private static final int COOLDOWN = 20; // no. cycles after which player can be launched again

    private final Map<Player, Integer> cooldowns = new HashMap<>();

    @Override
    public void run() {
        ConfigurationSection cs = Settings.get().velocityBlocks();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (cooldowns.containsKey(player)) {
                int newCooldown = cooldowns.get(player) - 1;

                if (newCooldown > 0) {
                    cooldowns.put(player, newCooldown);
                    continue;
                }

                cooldowns.remove(player);
            }

            Location loc = player.getLocation();
            String key = loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
            String velocity = cs.getString(key);

            if (velocity != null) {
                String[] parts = velocity.split(",");
                double x = Double.parseDouble(parts[0]), y = Double.parseDouble(parts[1]), z = Double.parseDouble(parts[2]);
                player.setVelocity(new Vector(x, y, z));
                cooldowns.put(player, COOLDOWN);
            }
        }
    }
}
