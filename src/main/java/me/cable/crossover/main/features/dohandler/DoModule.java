package me.cable.crossover.main.features.dohandler;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface DoModule {

    default void onEnter(@NotNull Player player) {}

    default void onLeave(@NotNull Player player) {}
}
