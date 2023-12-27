package me.cable.crossover.main.features.spleef;

import me.cable.crossover.main.object.Region;
import me.cable.crossover.main.util.ConfigHelper;
import me.cable.crossover.main.util.Message;
import me.cable.crossover.main.object.Minigame;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SpleefMinigame extends Minigame {

    private List<Player> allPlayers;
    private List<Player> alive;

    public SpleefMinigame(@NotNull ConfigurationSection instanceSettings) {
        super(instanceSettings);
    }

    @Override
    public @NotNull String id() {
        return "spleef";
    }

    @Override
    protected @NotNull Collection<Player> teleportOnEnd() {
        return alive;
    }

    @Override
    protected void cleanup() {
        placeBlocks();
    }

    private void onEliminate(@NotNull Player player) {
        if (settings().bool("elimination-teleport.enabled")) {
            Location loc = settings().loc("elimination-teleport.location", player.getWorld());
            player.teleport(loc);
        }

        getMessage("elimination")
                .placeholder("player", player.getName())
                .send(alive);
    }

    @Override
    protected void tick() {
        int eliminationY = settings().integer("elimination-y");

        for (Player player : List.copyOf(alive)) {
            if (!player.isOnline() || player.getLocation().getBlockY() <= eliminationY) {
                onEliminate(player);
                alive.remove(player);
            }
        }

        int remaining = alive.size();

        if (remaining <= 1) {
            Message message;

            if (remaining == 1) {
                message = getMessage("win").placeholder("player", alive.get(0).getName());
            } else {
                message = getMessage("no-win");
            }

            message.send(allPlayers);
            endGame();
        }
    }

    private void placeBlocks() {
        ConfigHelper blocksSection = settings().ch("blocks");
        String worldName = getWorldName();

        for (String key : blocksSection.getKeys(false)) {
            String[] parts = key.split(",");


            try {
                int x1 = Integer.parseInt(parts[0]), y1 = Integer.parseInt(parts[1]), z1 = Integer.parseInt(parts[2]),
                        x2 = Integer.parseInt(parts[3]), y2 = Integer.parseInt(parts[4]), z2 = Integer.parseInt(parts[5]);
                Material material = blocksSection.mat(key);
                new Region(worldName, x1, y1, z1, x2, y2, z2).fill(material);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
                // ignored
            }
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
        alive = new ArrayList<>(players);

        placeBlocks();
        teleportPlayers(players);
    }
}
