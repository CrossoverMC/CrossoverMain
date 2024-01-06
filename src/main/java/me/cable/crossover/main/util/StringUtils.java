package me.cable.crossover.main.util;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class StringUtils {

    @Contract("!null -> !null")
    public static @Nullable String format(@Nullable String string) {
        if (string == null) return null;
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static @NotNull List<String> format(@NotNull List<String> list) {
        List<String> formatted = new ArrayList<>();

        for (String s : list) {
            formatted.add(format(s));
        }

        return formatted;
    }

    public static @NotNull String replace(@NotNull String str, @NotNull Map<String, String> placeholders) {
        for (Entry<String, String> entry : placeholders.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null) {
                str = str.replace(entry.getKey(), entry.getValue());
            }
        }

        return str;
    }

    public static @NotNull List<String> replace(@NotNull List<String> list, @NotNull Map<String, String> placeholders) {
        List<String> replaced = new ArrayList<>();

        for (String s : list) {
            replaced.add(replace(s, placeholders));
        }

        return replaced;
    }
}
