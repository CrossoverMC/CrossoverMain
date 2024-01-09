package me.cable.crossover.main.util;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class Utils {

    public static @Nullable String playerNameFromUuid(@NotNull UUID playerUuid) {
        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            if (offlinePlayer.getUniqueId().equals(playerUuid)) {
                return offlinePlayer.getName();
            }
        }

        return null;
    }

    public static boolean hasBypass(@NotNull Player player) {
        return WorldGuard.getInstance().getPlatform().getSessionManager()
                .hasBypass(WorldGuardPlugin.inst().wrapPlayer(player), BukkitAdapter.adapt(player.getWorld()));
    }

    public static @NotNull String placeholder(@NotNull String name) {
        return '{' + name + '}';
    }

    public static @NotNull Vector getRelative(@NotNull Vector vector, @NotNull BlockFace blockFace, int amount) {
        return vector.clone().add(new Vector(amount * blockFace.getModX(),
                amount * blockFace.getModY(), amount * blockFace.getModZ()));
    }

    public static @NotNull BlockFace getLeftBlockFace(@NotNull BlockFace blockFace) {
        return switch (blockFace) {
            case NORTH -> BlockFace.WEST;
            case SOUTH -> BlockFace.EAST;
            case EAST -> BlockFace.NORTH;
            case WEST -> BlockFace.SOUTH;
            default -> throw new IllegalArgumentException("Invalid BlockFace " + blockFace);
        };
    }

    public static @NotNull BlockFace getRightBlockFace(@NotNull BlockFace blockFace) {
        return switch (blockFace) {
            case NORTH -> BlockFace.EAST;
            case SOUTH -> BlockFace.WEST;
            case EAST -> BlockFace.SOUTH;
            case WEST -> BlockFace.NORTH;
            default -> throw new IllegalArgumentException("Invalid BlockFace " + blockFace);
        };
    }

    public static float getYaw(@NotNull BlockFace blockFace) {
        return switch (blockFace) {
            case NORTH -> 180.0f;
            case SOUTH -> 0.0f;
            case EAST -> -90.0f;
            case WEST -> 90.0f;
            default -> throw new IllegalArgumentException("Invalid BlockFace " + blockFace);
        };
    }

    public static @NotNull Location locFromString(@NotNull String str) {
        String[] parts = str.split(",");
        World world = Bukkit.getWorld(parts[0]);
        float yaw = 0, pitch = 0;

        if (parts.length == 6) {
            yaw = Float.parseFloat(parts[4]);
            pitch = Float.parseFloat(parts[5]);
        }

        return new Location(world, Double.parseDouble(parts[1]), Double.parseDouble(parts[2]),
                Double.parseDouble(parts[3]), yaw, pitch);
    }

    @Contract("_, !null -> !null")
    public static @Nullable Location locFromString(@NotNull String str, @Nullable World world) {
        if (world == null) return null;

        String[] parts = str.split(",");
        float yaw = 0, pitch = 0;

        if (parts.length == 5) {
            yaw = Float.parseFloat(parts[3]);
            pitch = Float.parseFloat(parts[4]);
        }

        return new Location(world, Double.parseDouble(parts[0]), Double.parseDouble(parts[1]),
                Double.parseDouble(parts[2]), yaw, pitch);
    }

    public static @NotNull String formatDurationMillis(long milliseconds) {
        long minutes = (milliseconds / 1000) / 60;
        long seconds = (milliseconds / 1000) % 60;
        long millis = milliseconds % 1000;
        return minutes + "m:" + seconds + "s:" + millis + "ms";
    }

    public static @NotNull String formatDurationSeconds(int secs) {
        long days = secs / (24 * 3600);
        long hours = (secs % (24 * 3600)) / 3600;
        long minutes = (secs % 3600) / 60;
        long seconds = secs % 60;
        return days + "d:" + hours + "h:" + minutes + "m:" + seconds + "s";
    }
}
