package me.cable.crossover.main.shop;

import me.cable.crossover.main.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InvalidShopItem extends ShopItem {

    private final String type;

    public InvalidShopItem(@NotNull ConfigurationSection config, @Nullable String type) {
        super(config);
        this.type = type;
    }

    @Override
    public boolean hasItem(@NotNull Player player) {
        return true;
    }

    @Override
    public @NotNull ItemStack getItem(@NotNull Player player) {
        return new ItemBuilder().material(Material.BARRIER).name("&c&lInvalid Type: " + type).create();
    }

    @Override
    public void onPurchase(@NotNull Player player) {}
}
