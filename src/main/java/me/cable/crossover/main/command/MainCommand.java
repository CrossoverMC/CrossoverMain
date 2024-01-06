package me.cable.crossover.main.command;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import me.cable.crossover.main.CrossoverMain;
import me.cable.crossover.main.currency.Currency;
import me.cable.crossover.main.handler.MinigameConfigHandler;
import me.cable.crossover.main.handler.PlayerItems;
import me.cable.crossover.main.handler.SettingsConfigHandler;
import me.cable.crossover.main.handler.ShopConfigHandler;
import me.cable.crossover.main.util.Color;
import me.cable.crossover.main.util.Rest;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainCommand extends CustomCommand {

    private final SettingsConfigHandler settingsConfigHandler;
    private final MinigameConfigHandler minigameConfigHandler;
    private final ShopConfigHandler shopConfigHandler;

    public MainCommand(@NotNull CrossoverMain crossoverMain) {
        super(crossoverMain);
        settingsConfigHandler = crossoverMain.getSettingsConfigHandler();
        minigameConfigHandler = crossoverMain.getMinigameConfigHelper();
        shopConfigHandler = crossoverMain.getShopConfigHandler();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            PluginDescriptionFile pdf = crossoverMain.getDescription();
            sender.sendMessage(Color.SUCCESS + "Server is running " + pdf.getName() + " v" + pdf.getVersion() + ".");
            return true;
        }

        switch (args[0]) {
            case "currency" -> {
                String usage = Color.ERROR + "Usage: /" + label + " currency get <player> <currency> OR /"
                        + label + " currency <add|remove|set> <player> <currency> <amount>";

                if (args.length < 4) {
                    sender.sendMessage(usage);
                    return true;
                }

                String operation = args[1];
                String playerName = args[2];
                Player player = Bukkit.getPlayer(playerName);

                if (player == null) {
                    sender.sendMessage(Color.ERROR + "That player does not exist!");
                    return true;
                }

                UUID playerUuid = player.getUniqueId();
                String currencyId = args[3];
                Currency currency = Currency.getCurrencyIfExists(currencyId);

                if (currency == null) {
                    sender.sendMessage(Color.ERROR + "That currency does not exist!");
                    return true;
                }

                if (operation.equals("get")) {
                    BigDecimal amount = currency.get(playerUuid);
                    sender.sendMessage(Color.SPECIAL + playerName
                            + Color.SUCCESS + " has " + Color.SPECIAL + amount.toPlainString()
                            + Color.SUCCESS + " of the currency " + Color.SPECIAL + currencyId + Color.SUCCESS + ".");
                    return true;
                }
                if (args.length < 5) {
                    sender.sendMessage(usage);
                    return true;
                }

                BigDecimal amount;

                try {
                    amount = new BigDecimal(args[4]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(usage);
                    return true;
                }

                switch (operation) {
                    case "add" -> {
                        currency.deposit(playerUuid, amount);
                        sender.sendMessage(Color.SUCCESS + "Added " + Color.SPECIAL + amount.toPlainString()
                                + Color.SUCCESS + " to " + Color.SPECIAL + playerName + Color.SUCCESS + "'s "
                                + Color.SPECIAL + currencyId + Color.SUCCESS + " account.");
                    }
                    case "remove" -> {
                        currency.withdraw(playerUuid, amount);
                        sender.sendMessage(Color.SUCCESS + "Removed " + Color.SPECIAL + amount.toPlainString()
                                + Color.SUCCESS + " from " + Color.SPECIAL + playerName + Color.SUCCESS + "'s "
                                + Color.SPECIAL + currencyId + Color.SUCCESS + " account.");
                    }
                    case "set" -> {
                        currency.set(playerUuid, amount);
                        sender.sendMessage(Color.SUCCESS + "Set " + Color.SPECIAL + amount.toPlainString()
                                + Color.SUCCESS + " to " + Color.SPECIAL + playerName + Color.SUCCESS + "'s "
                                + Color.SPECIAL + currencyId + Color.SUCCESS + " account.");
                    }
                    default -> sender.sendMessage(usage);
                }
            }
            case "item" -> {
                String usage = Color.ERROR + "Usage: /" + label + " item give <player> <item> [amount]";

                if (args.length < 4) {
                    sender.sendMessage(usage);
                    return true;
                }

                String operation = args[1];
                String targetName = args[2];
                Player target = Bukkit.getPlayer(targetName);

                if (target == null) {
                    sender.sendMessage(Color.ERROR + "That player could not be found!");
                    return true;
                }

                String itemId = args[3];

                if (!PlayerItems.isValidItem(itemId)) {
                    sender.sendMessage(Color.ERROR + "The item type " + itemId + " is invalid!");
                    return true;
                }

                int amount = 1;

                if (args.length >= 5) {
                    try {
                        amount = Math.max(Integer.parseInt(args[4]), 1);
                    } catch (NumberFormatException ex) {
                        sender.sendMessage(Color.ERROR + "That player could not be found!");
                        return true;
                    }
                }

                PlayerItems playerItems = PlayerItems.getPlayerItems(target);

                switch (operation) {
                    case "give" -> {
                        playerItems.give(itemId, amount);
                        sender.sendMessage(Color.SUCCESS + "Gave " + Color.SPECIAL + amount + Color.SUCCESS + " of "
                                + Color.SPECIAL + itemId + Color.SUCCESS + " to "
                                + Color.SPECIAL + target.getName() + Color.SUCCESS + ".");
                    }
                    case "remove" -> {
                        playerItems.remove(itemId, amount);
                        sender.sendMessage(Color.SUCCESS + "Removed " + Color.SPECIAL + amount + Color.SUCCESS + " of "
                                + Color.SPECIAL + itemId + Color.SUCCESS + " from "
                                + Color.SPECIAL + target.getName() + Color.SUCCESS + ".");
                    }
                    default -> sender.sendMessage(usage);
                }
            }
            case "reload" -> {
                long millis = System.currentTimeMillis();
                Player player = (sender instanceof Player p) ? p : null;

                settingsConfigHandler.load(player);
                minigameConfigHandler.load(player);
                shopConfigHandler.load(player);

                sender.sendMessage(Color.SUCCESS + "Configuration reloaded in "
                        + Color.SPECIAL + (System.currentTimeMillis() - millis) + Color.SUCCESS + " ms.");
            }
            case "updatebooths" -> {
                StringBuilder sb = new StringBuilder();

                sender.sendMessage("Updating Discord voice channels. Please wait...");

                RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
                List<String> booths = new ArrayList<>();
                int totalRegions = 0;

                for (World world : Bukkit.getWorlds()) {
                    RegionManager regionManager = regionContainer.get(BukkitAdapter.adapt(world));
                    if (regionManager == null) continue;

                    for (ProtectedRegion region : regionManager.getRegions().values()) {
                        String booth = region.getFlag(CrossoverMain.BOOTH_FLAG);
                        if (booth == null) continue;

                        totalRegions++;

                        if (!booths.contains(booth)) {
                            booths.add(booth);
                            if (!sb.isEmpty()) sb.append(',');
                            sb.append(booth);
                        }
                    }
                }

                int finalTotalRegions = totalRegions;
                int totalBooths = booths.size();

                Rest.putRequest(Rest.HOST + "/update-booths", Map.of("booths", sb), res -> {
                    String statusMessage = Rest.getStatusMessage(res);

                    if ("success".equals(statusMessage)) {
                        sender.sendMessage(Color.SUCCESS + "Successfully updated Discord voice channels ("
                                + Color.SPECIAL + "" + finalTotalRegions + Color.SUCCESS + " region" + (finalTotalRegions == 1 ? "" : "s")
                                + ", " + Color.SPECIAL + totalBooths + Color.SUCCESS + " booth" + (totalBooths == 1 ? "" : "s") + ").");
                    } else {
                        sender.sendMessage(Color.ERROR + "Could not update Discord voice channels.");
                    }
                });
            }
            default -> sender.sendMessage(Color.ERROR + "Unknown sub-command!");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> list = new ArrayList<>();

        if (args.length == 1) {
            for (String s : List.of("currency", "item", "reload", "updatebooths")) {
                if (s.startsWith(args[0])) {
                    list.add(s);
                }
            }
        } else switch (args[0]) {
            case "currency" -> {
                switch (args.length) {
                    case 2 -> {
                        for (String s : List.of("add", "get", "remove", "set")) {
                            if (s.startsWith(args[1])) {
                                list.add(s);
                            }
                        }
                    }
                    case 3 -> {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            String name = player.getName();

                            if (name.startsWith(args[2])) {
                                list.add(name);
                            }
                        }
                    }
                    case 4 -> {
                        for (String currency : Currency.getCurrencies()) {
                            if (currency.startsWith(args[3])) {
                                list.add(currency);
                            }
                        }
                    }
                }
            }
            case "item" -> {
                switch (args.length) {
                    case 2 -> {
                        for (String s : List.of("give", "remove")) {
                            if (s.startsWith(args[1])) {
                                list.add(s);
                            }
                        }
                    }
                    case 3 -> {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            String name = player.getName();

                            if (name.startsWith(args[2])) {
                                list.add(name);
                            }
                        }
                    }
                    case 4 -> {
                        for (String s : PlayerItems.getItemTypes()) {
                            if (s.startsWith(args[3])) {
                                list.add(s);
                            }
                        }
                    }
                }
            }
        }

        return list;
    }
}
