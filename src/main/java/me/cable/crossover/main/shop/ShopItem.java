package me.cable.crossover.main.shop;

import me.cable.crossover.main.util.ConfigHelper;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class ShopItem {

    private static final Map<String, Function<ConfigurationSection, ShopItem>> shopItems = new HashMap<>();

    protected final ConfigHelper config;

    public ShopItem(@NotNull ConfigurationSection config) {
        this.config = new ConfigHelper(config);
    }

    public static void register(@NotNull String id, @NotNull Function<ConfigurationSection, ShopItem> supplier) {
        if (shopItems.containsKey(id)) {
            throw new IllegalStateException("Shop item with ID " + id + " has already been registered");
        }

        shopItems.put(id, supplier);
    }

    public static @NotNull ShopItem createShopItem(@NotNull ConfigurationSection cs) {
        String type = cs.getString("type");
        if (type == null) return new InvalidShopItem(cs, null);
        Function<ConfigurationSection, ShopItem> supplier = shopItems.get(type);
        return (supplier == null) ? new InvalidShopItem(cs, type) : supplier.apply(cs);
    }

    public final @NotNull String getCurrency() {
        return config.snn("currency");
    }

    public final @NotNull BigDecimal getPrice() {
        return BigDecimal.valueOf(config.doub("price"));
    }

    public final int getSlot() {
        return config.integer("slot");
    }

    public abstract boolean hasItem(@NotNull Player player);

    public abstract @NotNull ItemStack getItem(@NotNull Player player);

    public abstract void onPurchase(@NotNull Player player);
}
