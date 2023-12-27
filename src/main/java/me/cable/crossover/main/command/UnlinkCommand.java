package me.cable.crossover.main.command;

import me.cable.crossover.main.CrossoverMain;
import me.cable.crossover.main.util.Rest;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class UnlinkCommand extends CustomCommand {

    public UnlinkCommand(@NotNull CrossoverMain crossoverMain) {
        super(crossoverMain);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players may use this command!");
            return true;
        }

        sender.sendMessage("Please wait...");

        Rest.postRequest(Rest.HOST + "/unlink",
                Map.of("username", player.getName(), "uuid", player.getUniqueId().toString()), res -> {
                    String statusMessage = Rest.getStatusMessage(res);

                    switch (statusMessage) {
                        case "not_linked" ->
                                sender.sendMessage(ChatColor.RED + "Your account is not linked to a Discord account!");
                        case "success" -> {
                            String discordUsername = (String) res.get("discordUsername");
                            sender.sendMessage(ChatColor.GREEN + "Your account has been unlinked from the Discord account "
                                    + ChatColor.GOLD + discordUsername + ChatColor.GREEN + ".");
                        }
                        default -> sender.sendMessage(ChatColor.RED + "Failed to unlink your accounts.");
                    }
                });

        return true;
    }
}
