package me.cable.crossover.main.util;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public final class StringUtils {

    @Contract("!null -> !null")
    public static @Nullable String format(@Nullable String string) {
        if (string == null) return null;
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}
