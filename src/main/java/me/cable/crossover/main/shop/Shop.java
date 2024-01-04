package me.cable.crossover.main.shop;

import me.cable.crossover.main.currency.Currency;
import me.cable.crossover.main.menu.Menu;
import me.cable.crossover.main.util.Color;
import me.cable.crossover.main.util.ConfigHelper;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Shop extends Menu {

    private final ConfigHelper config;

    public Shop(@NotNull Player player, @NotNull ConfigurationSection config) {
        super(player);
        this.config = new ConfigHelper(config);

        handleCustomItems(this.config.csnn("custom-items"));

        List<ShopItem> shopItems = List.copyOf(createShopItems());

        render(inv -> {
            for (int i = 0; i < shopItems.size(); i++) {
                ShopItem shopItem = shopItems.get(i);
                ItemStack item = getItem(shopItem);
                tag(item, "ITEM_" + i);
                inv.setItem(shopItem.getSlot(), item);
            }
        });

        onClick((e, tag) -> {
            if (tag == null || !tag.startsWith("ITEM_")) return;

            int itemI = Integer.parseInt(tag.substring("ITEM_".length()));
            ShopItem shopItem = shopItems.get(itemI);

            if (shopItem.hasItem(player)) {
                player.sendMessage(Color.ERROR + "You already have this item!");
                return;
            }

            Currency currency = Currency.getCurrencyIfExists(shopItem.getCurrency());

            if (currency == null) {
                player.sendMessage(Color.ERROR + "Could not get price, please try again later!");
                return;
            }

            UUID playerUuid = player.getUniqueId();
            BigDecimal balance = currency.get(playerUuid);
            BigDecimal price = shopItem.getPrice();

            if (price.compareTo(balance) > 0) {
                player.sendMessage(Color.ERROR + "You do not have enough " + currency.name() + " for this!");
                return;
            }

            currency.withdraw(playerUuid, price);
            shopItem.onPurchase(player);
            player.sendMessage(Color.SUCCESS + "You have purchased an item for "
                    + Color.SPECIAL + currency.format(price, true) + Color.SUCCESS + ".");
            open();
        });
    }

    private @NotNull List<ShopItem> createShopItems() {
        List<ShopItem> shopItems = new ArrayList<>();
        ConfigHelper itemsSection = this.config.ch("items");

        for (String key : itemsSection.getKeys(false)) {
            ConfigHelper itemSection = itemsSection.ch(key);
            ShopItem shopItem = ShopItem.createShopItem(itemSection.getCs());
            shopItems.add(shopItem);
        }

        return shopItems;
    }

    private @NotNull ItemStack getItem(@NotNull ShopItem shopItem) {
        ItemStack item = shopItem.getItem(player);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            List<String> lore = meta.getLore();
            if (lore == null) lore = new ArrayList<>();

            if (!lore.isEmpty()) {
                lore.add("");
            }

            Currency currency = Currency.getCurrencyIfExists(shopItem.getCurrency());

            if (currency == null) {
                lore.add(Color.ERROR + "&lCould not get price");
            } else {
                lore.add(ChatColor.WHITE + "Cost: " + ChatColor.GOLD + currency.format(shopItem.getPrice(), true));
            }
            if (shopItem.hasItem(player)) {
                lore.add(Color.ERROR + "You already have this item!");
            } else {
                lore.add("" + ChatColor.GREEN + ChatColor.BOLD + "Click " + ChatColor.GRAY + "to purchase");
            }

            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    @Override
    protected @NotNull String title() {
        return config.snn("title");
    }

    @Override
    protected int rows() {
        return config.integer("rows");
    }
}
