package me.cable.crossover.main.features.antigravity;

import me.cable.crossover.main.util.Constants;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class AntigravityListener implements Listener {

    @EventHandler
    public void onPlayerInteract(@NotNull PlayerInteractEvent e) {
        if (!e.getAction().toString().startsWith("RIGHT_")) return;

        ItemStack item = e.getItem();
        if (item == null) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        String data = meta.getPersistentDataContainer().get(Constants.KEY_TOOL, PersistentDataType.STRING);
        if (data == null) return;

        Double multiplier = null;

        if (data.equals(Constants.TOOL_SMALL_JETPACK)) {
            multiplier = 1.0;
        } else if (data.equals(Constants.TOOL_LARGE_JETPACK)) {
            multiplier = 3.0;
        }
        if (multiplier != null) {
            Player player = e.getPlayer();
            e.setCancelled(true);
            player.setVelocity(player.getLocation().getDirection().multiply(multiplier));
        }
    }
}
