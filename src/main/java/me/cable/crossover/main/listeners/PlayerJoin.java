package me.cable.crossover.main.listeners;

import me.cable.crossover.main.CrossoverMain;
import me.cable.crossover.main.handler.MailHandler;
import me.cable.crossover.main.handler.SettingsConfigHandler;
import me.cable.crossover.main.util.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerJoin implements Listener {

    private final MailHandler mailHandler;

    public PlayerJoin(@NotNull CrossoverMain crossoverMain) {
        mailHandler = crossoverMain.getMailHandler();
    }

    @EventHandler
    public void event(@NotNull PlayerJoinEvent e) {
        Player player = e.getPlayer();
        mailHandler.sendMail(player);

        if (SettingsConfigHandler.getConfig().bool("spawn-location.enabled")) {
            Location loc = SettingsConfigHandler.getConfig().loc("spawn-location.location");
            if (loc != null) player.teleport(loc);
        }
        if (!Utils.hasBypass(player)) {
            player.getInventory().clear();
        }
    }
}
