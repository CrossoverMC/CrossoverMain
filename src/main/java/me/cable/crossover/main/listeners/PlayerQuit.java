package me.cable.crossover.main.listeners;

import me.cable.crossover.main.task.FallTeleportTask;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerQuit implements Listener {

    @EventHandler
    public void event(@NotNull PlayerJoinEvent e) {
        Player player = e.getPlayer();
        FallTeleportTask.lastGroundLocations.remove(player);
    }
}
