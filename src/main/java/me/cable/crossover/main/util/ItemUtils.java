package me.cable.crossover.main.util;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ItemUtils {

    public static void pd(@NotNull ItemStack item, @NotNull NamespacedKey key, @NotNull String str) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, str);
        item.setItemMeta(meta);
    }

    public static @Nullable String getStrPd(@Nullable ItemStack item, @NotNull NamespacedKey key) {
        if (item == null) return null;
        ItemMeta meta = item.getItemMeta();
        return (meta == null) ? null : meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
    }

    public static boolean hasPersistentData(@Nullable ItemStack item, @NotNull NamespacedKey key, @NotNull String value) {
        if (item == null) return false;
        ItemMeta meta = item.getItemMeta();
        return meta != null && value.equals(meta.getPersistentDataContainer().get(key, PersistentDataType.STRING));
    }
}
