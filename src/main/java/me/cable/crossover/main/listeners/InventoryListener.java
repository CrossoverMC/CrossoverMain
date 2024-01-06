package me.cable.crossover.main.listeners;

import me.cable.crossover.main.handler.PlayerItems;
import me.cable.crossover.main.playeritem.ItemType;
import me.cable.crossover.main.util.Constants;
import me.cable.crossover.main.util.ItemUtils;
import me.cable.crossover.main.util.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class InventoryListener implements Listener {

    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player player && !Utils.hasBypass(player)) {
            e.setCancelled(true);
        }

        ItemStack item = e.getCurrentItem();
        String playerItemId = ItemUtils.getStrPd(item, Constants.KEY_PLAYER_ITEM);

        if (playerItemId == null) {
            return;
        }

        ItemType itemType = PlayerItems.getItemType(playerItemId);

        if (itemType != null) {
            itemType.onClick(e);
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
