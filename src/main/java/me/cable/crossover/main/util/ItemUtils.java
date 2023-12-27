package me.cable.crossover.main.util;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ItemUtils {

    public static boolean hasPersistentData(@Nullable ItemStack item, @NotNull NamespacedKey key, @NotNull String value) {
        if (item == null) return false;
        ItemMeta meta = item.getItemMeta();
        return meta != null && value.equals(meta.getPersistentDataContainer().get(key, PersistentDataType.STRING));
    }
}
