package me.cable.crossover.main.command;

import me.cable.crossover.main.CrossoverMain;
import me.cable.crossover.main.util.Rest;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class LinkCommand extends CustomCommand {

    public LinkCommand(@NotNull CrossoverMain crossoverMain) {
        super(crossoverMain);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players may use this command!");
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "You must provide a code!");
            return true;
        }

        sender.sendMessage("Please wait...");

        String code = args[0];

        Rest.postRequest(Rest.HOST + "/link",
                Map.of("code", code, "username", player.getName(), "uuid", player.getUniqueId().toString()), res -> {
                    String statusMessage = Rest.getStatusMessage(res);

                    switch (statusMessage) {
                        case "already_linked" ->
                                sender.sendMessage(ChatColor.RED + "Your account has already been linked to a Discord account!");
                        case "invalid_code" -> sender.sendMessage(ChatColor.RED + "That code is not valid!");
                        case "success" -> {
                            String discordUsername = (String) res.get("discordUsername");
                            sender.sendMessage(ChatColor.GREEN + "Your account has been linked to the Discord account "
                                    + ChatColor.GOLD + discordUsername + ChatColor.GREEN + ".");
                        }
                        default -> sender.sendMessage(ChatColor.RED + "Failed to link your accounts.");
                    }
                });

        return true;
    }
}
