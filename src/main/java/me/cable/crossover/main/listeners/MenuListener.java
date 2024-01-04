package me.cable.crossover.main.listeners;

import me.cable.crossover.main.menu.Menu;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class MenuListener implements Listener {

    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent e) {
        Inventory inv = e.getClickedInventory();

        if (inv != null && inv.getHolder() instanceof Menu menu) {
            menu.onClickEvent(e);
        }
    }
}
