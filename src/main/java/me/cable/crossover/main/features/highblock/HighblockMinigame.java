package me.cable.crossover.main.features.highblock;

import me.cable.crossover.main.util.NumberUtils;
import me.cable.crossover.main.object.Minigame;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.Map.Entry;

public class HighblockMinigame extends Minigame {

    private Map<Player, Integer> playerScores;
    private int minX, minZ, minY, maxX, maxZ;
    private long timeStarted;

    public HighblockMinigame(@NotNull ConfigurationSection instanceSettings) {
        super(instanceSettings);
    }

    @Override
    public @NotNull String id() {
        return "highblock";
    }

    @Override
    protected @NotNull Collection<Player> teleportOnEnd() {
        return playerScores.keySet();
    }

    private void saveRegion() {
        String[] regionParts = settings().snn("region").split(",");
        int x1 = Integer.parseInt(regionParts[0]), z1 = Integer.parseInt(regionParts[1]),
                x2 = Integer.parseInt(regionParts[2]), z2 = Integer.parseInt(regionParts[3]);
        minX = Math.min(x1, x2);
        minY = settings().integer("min-y");
        minZ = Math.min(z1, z2);
        maxX = Math.max(x1, x2);
        maxZ = Math.max(z1, z2);
    }

    private int getDuration() {
        return settings().integer("duration");
    }

    public boolean isInGame(@NotNull Location loc) {
        int x = loc.getBlockX(), y = loc.getBlockY(), z = loc.getBlockZ();
        return x >= minX && x <= maxX && y >= minY && z >= minZ && z <= maxZ;
    }

    @Override
    protected void cleanup() {
        World world = getWorld();
        if (world == null) return;

        // falling
        for (FallingBlock fallingBlock : world.getEntitiesByClass(FallingBlock.class)) {
            Location loc = fallingBlock.getLocation();

            if (isInGame(loc)) {
                fallingBlock.remove();
            }
        }

        // block
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                int maxY = world.getHighestBlockYAt(x, z);

                for (int y = minY; y <= maxY; y++) {
                    world.getBlockAt(x, y, z).setType(Material.AIR);
                }
            }
        }

        playerScores = null;
    }

    public @NotNull List<Entry<Player, Integer>> getTopScores() {
        if (playerScores == null) return Collections.emptyList();
        List<Entry<Player, Integer>> list = new ArrayList<>(playerScores.entrySet());
        list.sort((a, b) -> b.getValue() - a.getValue());
        return list;
    }

    private void announceWinners() {
        for (Player player : playerScores.keySet()) {
            List<Entry<Player, Integer>> topScores = getTopScores();
            getMessage("winners-top").send(player);

            for (int i = 0; i < Math.min(topScores.size(), 3); i++) {
                Entry<Player, Integer> entry = topScores.get(i);
                getMessage("winner")
                        .placeholder("player", entry.getKey().getName())
                        .placeholder("pos", Integer.toString(i + 1))
                        .placeholder("score", Integer.toString(entry.getValue()))
                        .placeholder("score_plural", entry.getValue() == 1 ? "" : "s")
                        .send(player);
            }

            getMessage("winners-bottom").send(player);
        }
    }

    public int getRemainingSeconds() {
        int passed = (int) ((System.currentTimeMillis() - timeStarted) / 1000.0);
        return Math.max(getDuration() - passed, 0);
    }

    @Override
    protected void startGame(@NotNull List<Player> players) {
        timeStarted = System.currentTimeMillis();
        playerScores = new HashMap<>();

        World world = getWorld();
        saveRegion();
        Location spawnLoc = new Location(world, (maxX + minX + 1) / 2.0, minY, (maxZ + minZ + 1) / 2.0);

        for (Player player : players) {
            playerScores.put(player, 0);
            if (world != null) player.teleport(spawnLoc);
        }

        Material material = settings().mat("material");
        BukkitTask tickTask = new BukkitRunnable() {

            @Override
            public void run() {
                if (world == null) return;
                int relative = settings().integer("drop-height") + 1;

                for (int i = 0; i < settings().integer("blocks-per-tick"); i++) {
                    int x = NumberUtils.random(minX, maxX);
                    int z = NumberUtils.random(minZ, maxZ);
                    world.getHighestBlockAt(x, z).getRelative(BlockFace.UP, relative).setType(material);
                }
            }
        }.runTaskTimer(crossoverMain, 0, 1);
        BukkitTask secondTask = new BukkitRunnable() {

            @Override
            public void run() {
                Map<Integer, List<Player>> heights = new HashMap<>();

                // remove exited & store heights
                for (Player player : Map.copyOf(playerScores).keySet()) {
                    Location loc = player.getLocation();

                    if (player.isOnline() && isInGame(loc)) {
                        int height = loc.getBlockY() - minY;
                        heights.computeIfAbsent(height, h -> new ArrayList<>()).add(player);
                    } else {
                        playerScores.remove(player);
                    }
                }

                List<Entry<Integer, List<Player>>> list = new ArrayList<>(heights.entrySet());
                list.sort(Comparator.comparingInt(Entry::getKey));

                // give points
                for (int i = 0; i < list.size(); i++) {
                    for (Player player : list.get(i).getValue()) {
                        playerScores.put(player, playerScores.get(player) + i + 1);
                    }
                }
            }
        }.runTaskTimer(crossoverMain, 20, 20);

        Bukkit.getScheduler().scheduleSyncDelayedTask(crossoverMain, () -> {
            tickTask.cancel();
            secondTask.cancel();
            announceWinners();
            endGame();
        }, getDuration() * 20L);
    }
}
