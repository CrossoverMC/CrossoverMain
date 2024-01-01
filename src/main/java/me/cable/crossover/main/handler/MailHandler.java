package me.cable.crossover.main.handler;

import me.cable.crossover.main.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class MailHandler {

    private static final String MAIL_PATH = "mail";

    public void sendMail(@NotNull UUID playerUuid, @NotNull List<String> messages) {
        Player player = Bukkit.getPlayer(playerUuid);

        if (player == null) {
            // store
            YamlConfiguration config = PlayerData.get(playerUuid);
            List<String> currentMail = config.getStringList(MAIL_PATH);
            currentMail.addAll(messages);
            config.set(MAIL_PATH, currentMail);
        } else {
            // send
            new Message(messages).send(player);
        }
    }

    public void sendMail(@NotNull Player player) {
        YamlConfiguration config = PlayerData.get(player.getUniqueId());
        List<String> mail = config.getStringList(MAIL_PATH);
        config.set(MAIL_PATH, null);
        new Message(mail).send(player);
    }
}
