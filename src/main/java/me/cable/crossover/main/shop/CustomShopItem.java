package me.cable.crossover.main.shop;

import me.cable.crossover.main.util.ItemBuilder;
import me.cable.crossover.main.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CustomShopItem extends ShopItem {

    public CustomShopItem(@NotNull ConfigurationSection config) {
        super(config);
    }

    @Override
    public boolean hasItem(@NotNull Player player) {
        return false;
    }

    @Override
    public @NotNull ItemStack getItem(@NotNull Player player) {
        return new ItemBuilder().config(config.csnn("item")).create();
    }

    @Override
    public void onPurchase(@NotNull Player player) {
        List<String> commands = config.strList("on-purchase-commands");
        String playerName = player.getName();

        for (String command : commands) {
            command = command.replace(Utils.placeholder("player"), playerName);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }
}
