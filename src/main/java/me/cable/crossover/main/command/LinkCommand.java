package me.cable.crossover.main.command;

import me.cable.crossover.main.CrossoverMain;
import me.cable.crossover.main.util.Color;
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
            sender.sendMessage(Color.ERROR + "Only players may use this command!");
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(Color.ERROR + "You must provide a code!");
            return true;
        }

        sender.sendMessage("Please wait...");

        String code = args[0];

        Rest.postRequest(Rest.HOST + "/link",
                Map.of("code", code, "username", player.getName(), "uuid", player.getUniqueId().toString()), res -> {
                    String statusMessage = Rest.getStatusMessage(res);

                    switch (statusMessage) {
                        case "already_linked" ->
                                sender.sendMessage(Color.ERROR + "Your account has already been linked to a Discord account!");
                        case "invalid_code" -> sender.sendMessage(Color.ERROR + "That code is not valid!");
                        case "success" -> {
                            String discordUsername = (String) res.get("discordUsername");
                            sender.sendMessage(Color.SUCCESS + "Your account has been linked to the Discord account "
                                    + Color.SPECIAL + discordUsername + Color.SUCCESS + ".");
                        }
                        default -> sender.sendMessage(Color.ERROR + "Failed to link your accounts.");
                    }
                });

        return true;
    }
}
