package me.cable.crossover.main.listeners;

import me.cable.crossover.main.menu.MainMenu;
import me.cable.crossover.main.util.Constants;
import me.cable.crossover.main.util.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlayerInteract implements Listener {

    @EventHandler
    public void event(@NotNull PlayerInteractEvent e) {
        ItemStack item = e.getItem();

        if (ItemUtils.hasPersistentData(item, Constants.TOOL_KEY, Constants.TOOL_MAIN_MENU)) {
            Player player = e.getPlayer();

            e.setCancelled(true);
            new MainMenu(player).open();
        }
    }
}
