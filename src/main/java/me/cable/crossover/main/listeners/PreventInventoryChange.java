package me.cable.crossover.main.listeners;

import me.cable.crossover.main.util.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.jetbrains.annotations.NotNull;

public class PreventInventoryChange implements Listener {

    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player player && !Utils.hasBypass(player)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(@NotNull PlayerDropItemEvent e) {
        if (!Utils.hasBypass(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerSwapHandItems(@NotNull PlayerSwapHandItemsEvent e) {
        if (!Utils.hasBypass(e.getPlayer())) {
            e.setCancelled(true);
        }
    }
}
