package me.cable.crossover.main.features.booth;

import me.cable.crossover.main.util.ItemBuilder;
import me.cable.crossover.main.util.ItemUtils;
import me.cable.crossover.main.util.Rest;
import me.cable.crossover.main.util.Keys;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class BoothListener implements Listener {

    private static final int CONNECT_COOLDOWN = 2000; // millis

    private final Map<Player, Long> lastConnections = new HashMap<>();

    @EventHandler
    public void onPlayerInteract(@NotNull PlayerInteractEvent e) {
        if (!e.getAction().toString().startsWith("RIGHT_")) return;

        ItemStack item = e.getItem();
        if (!ItemUtils.hasPersistentData(item, Keys.TOOL, Keys.TOOL_BOOTH)) return;

        e.setCancelled(true);

        Player player = e.getPlayer();
        String currentBooth = BoothHandler.playerBooths.get(player);
        if (currentBooth == null) return;

        Long lastConnection = lastConnections.get(player);

        if (lastConnection != null && System.currentTimeMillis() - lastConnection <= CONNECT_COOLDOWN) {
            player.sendMessage(ChatColor.RED + "Please wait before using this again!");
            return;
        }

        player.sendMessage("Joining voice channel, please wait...");
        BoothHandler.potentiallyConnected.add(player);
        lastConnections.put(player, System.currentTimeMillis());

        // join voice
        Rest.putRequest(Rest.HOST + "/voice-booth",
                Map.of("booth", currentBooth, "user", player.getUniqueId().toString()), res -> {
                    String statusMessage = Rest.getStatusMessage(res);

                    switch (statusMessage) {
                        case "not_linked" ->
                                player.sendMessage(ChatColor.RED + "Could not put you into the booth's Discord voice channel because your account is not linked to a Discord account!");
                        case "success" -> {
                            if (BoothHandler.potentiallyConnected.contains(player)) {
                                player.sendMessage(ChatColor.GREEN + "You are now in this booth's Discord voice channel.");
                                player.getInventory().setItem(Keys.PRIMARY_SLOT, new ItemBuilder().material(Material.LIME_DYE)
                                        .name("&a&lIn Voice")
                                        .lore("&7You are in this booth's &9Discord",
                                                "&7voice channel. Leave the booth",
                                                "&7to leave the voice channel.")
                                        .create());
                            } else {
                                // left booth during connection
                                player.sendMessage(ChatColor.RED + "You were not put in the voice channel because you left the booth!");
                            }
                        }
                        case "user_unready" ->
                                player.sendMessage(ChatColor.RED + "To join this booth's Discord voice channel, connect to the Waiting channel!");
                        default ->
                                player.sendMessage(ChatColor.RED + "Failed to put you in the booth's Discord voice channel.");
                    }
                });
    }

    @EventHandler
    public void onPlayerQuit(@NotNull PlayerQuitEvent e) {
        Player player = e.getPlayer();
        lastConnections.remove(player);
    }
}
