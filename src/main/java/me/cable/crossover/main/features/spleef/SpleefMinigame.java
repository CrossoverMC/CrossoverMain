package me.cable.crossover.main.features.spleef;

import me.cable.crossover.main.object.Minigame;
import me.cable.crossover.main.object.Region;
import me.cable.crossover.main.util.ConfigHelper;
import me.cable.crossover.main.util.Message;
import me.cable.crossover.main.util.StringUtils;
import me.cable.crossover.main.util.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SpleefMinigame extends Minigame {

    private List<Player> allPlayers;
    private List<Player> alivePlayers;
    private boolean started;

    public SpleefMinigame(@NotNull ConfigurationSection instanceSettings) {
        super(instanceSettings);
    }

    @Override
    public @NotNull String id() {
        return "spleef";
    }

    @Override
    protected @NotNull Collection<Player> teleportOnEnd() {
        return alivePlayers;
    }

    @Override
    protected void cleanup() {
        placeBlocks(false);
    }

    private void onEliminate(@NotNull Player player) {
        // teleport
        if (settings().bool("elimination-teleport.enabled")) {
            Location loc = settings().loc("elimination-teleport.location", player.getWorld());
            player.teleport(loc);
        }

        // message
        getMessage("elimination")
                .placeholder("player", player.getName())
                .send(allPlayers);
    }

    private void placeBlocks(boolean game) {
        ConfigHelper blocksSection = settings().ch("blocks");
        String worldName = getWorldName();

        for (String key : blocksSection.getKeys(false)) {
            String[] parts = key.split(",");

            try {
                int x1 = Integer.parseInt(parts[0]), y1 = Integer.parseInt(parts[1]), z1 = Integer.parseInt(parts[2]),
                        x2 = Integer.parseInt(parts[3]), y2 = Integer.parseInt(parts[4]), z2 = Integer.parseInt(parts[5]);
                Material material = blocksSection.mat(key + "." + (game ? "game" : "original"));
                new Region(worldName, x1, y1, z1, x2, y2, z2).fill(material);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
                // ignored
            }
        }
    }

    @Override
    protected void tick() {
        if (!started) return;

        int eliminationY = settings().integer("elimination-y");

        for (Player player : List.copyOf(alivePlayers)) {
            if (!player.isOnline() || player.getLocation().getBlockY() <= eliminationY) {
                if (player.isOnline()) onEliminate(player);
                alivePlayers.remove(player);
            }
        }

        int remaining = alivePlayers.size();

        if (remaining <= 1) {
            Message message;

            if (remaining == 1) {
                message = getMessage("win").placeholder("player", alivePlayers.get(0).getName());
            } else {
                message = getMessage("no-win");
            }

            message.send(allPlayers);
            endGame();
        }
    }

    private void teleportPlayers(@NotNull List<Player> players) {
        List<Location> spawns = new ArrayList<>();
        World world = getWorld();

        if (world != null) {
            List<String> spawnsStringList = settings().strList("spawns");
            if (spawnsStringList.isEmpty()) return;

            for (String spawn : spawnsStringList) {
                String[] parts = spawn.split(",");

                try {
                    double x = Double.parseDouble(parts[0]), y = Double.parseDouble(parts[1]), z = Double.parseDouble(parts[2]);
                    float yaw = Float.parseFloat(parts[3]), pitch = Float.parseFloat(parts[4]);
                    spawns.add(new Location(world, x, y, z, yaw, pitch));
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
                    // ignored
                }
            }
        }

        Collections.shuffle(spawns);
        int spawnsI = 0;

        for (Player player : players) {
            player.teleport(spawns.get(spawnsI++));
            if (spawnsI >= spawns.size()) spawnsI = 0;
        }
    }

    @Override
    protected void startGame(@NotNull List<Player> players) {
        allPlayers = new ArrayList<>(players);
        alivePlayers = new ArrayList<>(players);
        started = false;

        placeBlocks(true);
        teleportPlayers(players);

        new BukkitRunnable() {

            int countdown = minigameSettings().integer("countdown");

            @Override
            public void run() {
                if (countdown > 0) {
                    String countdownTitle = minigameSettings().snn("countdown-title");
                    countdownTitle = StringUtils.format(countdownTitle.replace(
                            Utils.placeholder("countdown"), Integer.toString(countdown)));

                    for (Player player : alivePlayers) {
                        player.sendTitle(countdownTitle, null, 0, 30, 0);
                    }

                    countdown--;
                } else {
                    String startTitle = StringUtils.format(minigameSettings().snn("start-title"));

                    for (Player player : alivePlayers) {
                        player.sendTitle(startTitle, null, 0, 15, 5);
                    }

                    started = true;
                    cancel();
                }
            }
        }.runTaskTimer(crossoverMain, 20, 20);
    }

    @EventHandler
    private void onPlayerMove(@NotNull PlayerMoveEvent e) {
        if (started) return;

        Player player = e.getPlayer();
        if (!alivePlayers.contains(player)) return;

        Location to = e.getTo();

        if (to != null && !e.getFrom().toVector().equals(to.toVector())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    private void onBlockBreak(@NotNull BlockBreakEvent e) {
        Player player = e.getPlayer();

        if (!started && alivePlayers.contains(player)) {
            e.setCancelled(true);
        }
    }
}
