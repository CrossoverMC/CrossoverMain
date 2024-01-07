package me.cable.crossover.main.listeners;

import me.cable.crossover.main.features.playerspeed.SpeedModifier;
import me.cable.crossover.main.task.FallTeleportTask;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerQuit implements Listener {

    @EventHandler
    public void event(@NotNull PlayerQuitEvent e) {
        Player player = e.getPlayer();
        FallTeleportTask.lastGroundLocations.remove(player);
        SpeedModifier.removePlayerModifiers(player);
    }
}
