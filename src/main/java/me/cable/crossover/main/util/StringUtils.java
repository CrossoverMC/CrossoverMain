package me.cable.crossover.main.util;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Map.Entry;

public final class StringUtils {

    @Contract("!null -> !null")
    public static @Nullable String format(@Nullable String string) {
        if (string == null) return null;
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static @NotNull String replace(@NotNull String str, @NotNull Map<String, String> placeholders) {
        for (Entry<String, String> entry : placeholders.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null) {
                str = str.replace(entry.getKey(), entry.getValue());
            }
        }

        return str;
    }
}
