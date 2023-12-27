package me.cable.crossover.main.util;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class Utils {

    public static boolean hasBypass(@NotNull Player player) {
        return WorldGuard.getInstance().getPlatform().getSessionManager()
                .hasBypass(WorldGuardPlugin.inst().wrapPlayer(player), BukkitAdapter.adapt(player.getWorld()));
    }

    public static void playSound(@NotNull Player player, @NotNull Sound sound) {
        player.playSound(player.getLocation(), sound, Float.MAX_VALUE, 1);
    }
}
