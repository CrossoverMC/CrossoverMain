package me.cable.crossover.main.object;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class Region {

    private final @NotNull String worldName;
    private final int minX, minY, minZ, maxX, maxY, maxZ;

    public Region(@NotNull String worldName, int x1, int y1, int z1, int x2, int y2, int z2) {
        this.worldName = worldName;
        minX = Math.min(x1, x2);
        minY = Math.min(y1, y2);
        minZ = Math.min(z1, z2);
        maxX = Math.max(x1, x2);
        maxY = Math.max(y1, y2);
        maxZ = Math.max(z1, z2);
    }

    public Region(@NotNull Region region) {
        this(region.getWorldName(), region.getMinX(), region.getMinY(), region.getMinZ(),
                region.getMaxX(), region.getMaxY(), region.getMaxZ());
    }

    public static @NotNull Region deserialize(@NotNull ConfigurationSection cs) {
        return new Region(
                Objects.requireNonNull(cs.getString("world")),
                cs.getInt("x1"),
                cs.getInt("y1"),
                cs.getInt("z1"),
                cs.getInt("x2"),
                cs.getInt("y2"),
                cs.getInt("z2")
        );
    }

    public @Nullable Location getCenter() {
        World world = getWorld();
        return (world == null) ? null : new Location(world, (minX + maxX + 1) / 2.0,
                (minY + maxY + 1) / 2.0, (minZ + maxZ + 1) / 2.0);
    }

    public @Nullable Location getBottomCenter() {
        World world = getWorld();
        return (world == null) ? null : new Location(world, (minX + maxX + 1) / 2.0, minY, (minZ + maxZ + 1) / 2.0);
    }

    public boolean contains(double x, double y, double z) {
        return x >= minX && x < maxX + 1
                && y >= minY && y < maxY + 1
                && z >= minZ && z < maxZ + 1;
    }

    public boolean contains(@NotNull World world, double x, double y, double z) {
        return world.getName().equals(worldName) && contains(x, y, z);
    }

    public boolean contains(@NotNull Block block) {
        return contains(block.getWorld(), block.getX(), block.getY(), block.getZ());
    }

    public boolean contains(@NotNull Location loc) {
        World world = loc.getWorld();
        return (world != null) && contains(world, loc.getX(), loc.getY(), loc.getZ());
    }

    public boolean contains(@NotNull Entity entity) {
        return contains(entity.getLocation());
    }

    public @NotNull List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (contains(player)) {
                players.add(player);
            }
        }

        return players;
    }

    public void forEachBlock(@NotNull Consumer<Block> consumer) {
        World world = getWorld();
        if (world == null) return;

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    consumer.accept(block);
                }
            }
        }
    }

    public void fill(@NotNull Material material) {
        forEachBlock(b -> b.setType(material));
    }

    public void clear() {
        fill(Material.AIR);
    }

    public void serialize(@NotNull ConfigurationSection cs) {
        cs.set("world", worldName);
        cs.set("x1", minX);
        cs.set("y1", minY);
        cs.set("z1", minZ);
        cs.set("x2", maxX);
        cs.set("y2", maxY);
        cs.set("z2", maxZ);
    }

    public @NotNull String getWorldName() {
        return worldName;
    }

    public @Nullable World getWorld() {
        return Bukkit.getWorld(worldName);
    }

    public int getMinX() {
        return minX;
    }

    public int getMinY() {
        return minY;
    }

    public int getMinZ() {
        return minZ;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMaxY() {
        return maxY;
    }

    public int getMaxZ() {
        return maxZ;
    }
}
