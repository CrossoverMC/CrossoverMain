package me.cable.crossover.main.util;

import me.cable.crossover.main.CrossoverMain;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public final class Constants {

    public static final int PRIMARY_SLOT = 0;
    public static final int SECONDARY_SLOT = 1;
    public static final int TERTIARY_SLOT = 2;
    public static final int QUATERNARY_SLOT = 3;

    public static final NamespacedKey TOOL_KEY = createKey("tool");

    public static final String TOOL_MAIN_MENU = "MAIN_MENU";
    public static final String TOOL_BOOTH = "BOOTH";
    public static final String TOOL_LARGE_JETPACK = "LARGE_JETPACK";
    public static final String TOOL_SMALL_JETPACK = "WEAK_JETPACK";
    public static final String TOOL_LEVEL_1 = "LEVEL_1";
    public static final String TOOL_LEVEL_2 = "LEVEL_2";
    public static final String TOOL_LEVEL_3 = "LEVEL_3";

    private static @NotNull NamespacedKey createKey(@NotNull String s) {
        return new NamespacedKey(CrossoverMain.getInstance(), s);
    }
}
