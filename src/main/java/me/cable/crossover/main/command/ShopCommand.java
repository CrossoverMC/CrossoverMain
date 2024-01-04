package me.cable.crossover.main.command;

import me.cable.crossover.main.CrossoverMain;
import me.cable.crossover.main.handler.ShopConfigHandler;
import me.cable.crossover.main.shop.Shop;
import me.cable.crossover.main.util.Color;
import me.cable.crossover.main.util.ConfigHelper;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ShopCommand extends CustomCommand {

    public ShopCommand(@NotNull CrossoverMain crossoverMain) {
        super(crossoverMain);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Color.ERROR + "Usage: /" + label + " <shop> [player]");
            return true;
        }

        String shopId = args[0];
        Player target;

        if (args.length >= 2) {
            String targetName = args[1];
            target = Bukkit.getPlayer(targetName);

            if (target == null) {
                sender.sendMessage(Color.ERROR + "The player " + targetName + " could not be found!");
                return true;
            }
        } else {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(Color.ERROR + "Provide a player!");
                return true;
            }

            target = player;
        }

        ConfigHelper config = ShopConfigHandler.getConfig();

        if (config.isSet(shopId)) {
            new Shop(target, config.csnn(shopId)).open();
        } else {
            sender.sendMessage(Color.ERROR + "That shop does not exist!");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> list = new ArrayList<>();

        switch (args.length) {
            case 1 -> {
                for (String a : ShopConfigHandler.getShopIds()) {
                    if (a.startsWith(args[0])) {
                        list.add(a);
                    }
                }
            }
            case 2 -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    String a = player.getName();

                    if (a.startsWith(args[1])) {
                        list.add(a);
                    }
                }
            }
        }

        return list;
    }
}
