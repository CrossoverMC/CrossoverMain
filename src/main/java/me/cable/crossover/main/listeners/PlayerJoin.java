package me.cable.crossover.main.listeners;

import me.cable.crossover.main.CrossoverMain;
import me.cable.crossover.main.handler.MailHandler;
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
    public void onInventoryClick(@NotNull PlayerJoinEvent e) {
        Player player = e.getPlayer();
        mailHandler.sendMail(player);
    }
}
