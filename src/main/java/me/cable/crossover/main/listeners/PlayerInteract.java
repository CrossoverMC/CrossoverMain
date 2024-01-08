package me.cable.crossover.main.listeners;

import me.cable.crossover.main.handler.InventoryItems;
import me.cable.crossover.main.inventoryitem.EquippableItem;
import me.cable.crossover.main.inventoryitem.InventoryItem;
import me.cable.crossover.main.menu.MainMenu;
import me.cable.crossover.main.util.Constants;
import me.cable.crossover.main.util.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlayerInteract implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void event(@NotNull PlayerInteractEvent e) {
        ItemStack item = e.getItem();

        if (ItemUtils.hasPersistentData(item, Constants.KEY_TOOL, Constants.TOOL_MAIN_MENU)
                && e.getAction().toString().startsWith("RIGHT_")) {
            Player player = e.getPlayer();

            e.setCancelled(true);
            new MainMenu(player).open();
        } else {
            String playerItemId = ItemUtils.getStrPd(item, Constants.KEY_PLAYER_ITEM);

            if (playerItemId != null) {
                e.setCancelled(true);

                InventoryItem inventoryItem = InventoryItems.getItemType(playerItemId);

                if (inventoryItem instanceof EquippableItem equippableItem) {
                    equippableItem.onInteract(e);
                }
            }
        }
    }
}
