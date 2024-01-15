package me.cable.crossover.main.object;

import me.cable.crossover.main.CrossoverMain;
import me.cable.crossover.main.features.playerspeed.SpeedModifier;
import me.cable.crossover.main.features.playerspeed.SpeedPriority;
import me.cable.crossover.main.handler.MinigameConfigHandler;
import me.cable.crossover.main.util.ConfigHelper;
import me.cable.crossover.main.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class Minigame implements Listener {

    private static final List<Minigame> registered = new ArrayList<>();

    protected final CrossoverMain crossoverMain;

    protected final String id;
    private final ConfigurationSection instanceSettings;

    private int countdown;
    private boolean waiting;
    private boolean gameRunning;
    private List<SpeedModifier> activeSpeedModifiers;

    private static @NotNull ConfigHelper getGlobalMinigameSettings() {
        return MinigameConfigHandler.getConfig().ch("settings");
    }

    public static void initialize(@NotNull CrossoverMain crossoverMain) {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(crossoverMain, () -> {
            for (Minigame minigame : registered) {
                if (minigame.gameRunning) continue;

                Region waitingRegion = minigame.getWaitingRegion();
                List<Player> players = waitingRegion.getPlayers();

                if (players.size() < minigame.getMinPlayers()) {
                    if (!minigame.waiting) {
                        minigame.waiting = true;
                        minigame.countdown = 0;
                    }
                    if (minigame.countdown <= 0) {
                        minigame.countdown = getGlobalMinigameSettings().integer("waiting-message-period");
                        getGlobalMinigameSettings().message("waiting-message").send(players);
                    }
                } else {
                    if (minigame.waiting) {
                        minigame.waiting = false;
                        minigame.countdown = minigame.getCountdown();
                    }
                    if (minigame.countdown <= 0) {
                        // start minigame
                        getGlobalMinigameSettings().message("start-message").send(players);
                        Bukkit.getPluginManager().registerEvents(minigame, minigame.crossoverMain);
                        minigame.startGame(players);
                        minigame.gameRunning = true;

                        // default speed modifiers
                        minigame.activeSpeedModifiers = new ArrayList<>();

                        for (Player player : players) {
                            SpeedModifier speedModifier = minigame.speedModifier(player);

                            if (speedModifier != null) {
                                speedModifier.attachWalk();
                                minigame.activeSpeedModifiers.add(speedModifier);
                            }
                        }
                    } else if (minigame.countdown % 10 == 0 || minigame.countdown <= 5) {
                        getGlobalMinigameSettings().message("starting-message")
                                .placeholder("time", Integer.toString(minigame.countdown))
                                .placeholder("time_plural", minigame.countdown == 1 ? "" : "s")
                                .send(players);
                    }
                }

                minigame.countdown--;
            }
        }, 0, 20);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(crossoverMain, () -> {
            for (Minigame minigame : registered) {
                if (minigame.gameRunning) {
                    minigame.tick();
                }
            }
        }, 0, 1);
    }

    public Minigame(@NotNull ConfigurationSection instanceSettings) {
        this.instanceSettings = instanceSettings;
        crossoverMain = CrossoverMain.getInstance();
        id = id();
    }

    public abstract @NotNull String id();

    /* Registration */

    public static void register(@NotNull Minigame minigame) {
        if (registered.contains(minigame)) {
            throw new IllegalStateException("Minigame has already been registered");
        }

        registered.add(minigame);
    }

    public static void unregisterAll() {
        for (Minigame minigame : registered) {
            if (minigame.gameRunning) {
                minigame.endGame();
            }
        }

        registered.clear();
    }

    public static @NotNull List<Minigame> getRegisteredMinigames() {
        return List.copyOf(registered);
    }

    /* Settings */

    protected @NotNull ConfigHelper minigameSettings() {
        return MinigameConfigHandler.getConfig().ch(MinigameConfigHandler.MINIGAMES_PATH + "." + id + ".settings");
    }

    protected final @NotNull Message getMessage(@NotNull String path) {
        return minigameSettings().message("messages." + path);
    }

    private @NotNull ConfigHelper getInstanceSettings() {
        return new ConfigHelper(instanceSettings);
    }

    protected final int getCountdown() {
        return getInstanceSettings().integer("countdown");
    }

    protected final int getMinPlayers() {
        return getInstanceSettings().integer("min-players");
    }

    protected final @NotNull String getWorldName() {
        return getInstanceSettings().snn("world");
    }

    protected final @Nullable World getWorld() {
        return Bukkit.getWorld(getWorldName());
    }

    protected final @NotNull Region getWaitingRegion() {
        String coords = getInstanceSettings().snn("waiting-region");
        String[] parts = coords.split(",");
        return new Region(getWorldName(), Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]),
                Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), Integer.parseInt(parts[5]));
    }

    protected final @NotNull ConfigHelper settings() {
        return getInstanceSettings().ch("minigame");
    }

    /* Minigame */

    protected @NotNull Collection<Player> teleportOnEnd() {
        return Collections.emptyList();
    }

    protected @Nullable SpeedModifier speedModifier(@NotNull Player player) {
        return new SpeedModifier(player, SpeedModifier.DEFAULT_WALK, SpeedPriority.HIGH);
    }

    protected final void detachSpeedModifier(@NotNull Player player) {
        for (SpeedModifier speedModifier : activeSpeedModifiers) {
            if (speedModifier.getPlayer().equals(player)) {
                speedModifier.detachWalk();
                break;
            }
        }
    }

    protected void tick() {}

    protected final void endGame() {
        Location waitingLoc = getWaitingRegion().getBottomCenter();

        if (waitingLoc != null) {
            for (Player player : teleportOnEnd()) {
                player.teleport(waitingLoc);
            }
        }

        activeSpeedModifiers.forEach(SpeedModifier::detachWalk);
        activeSpeedModifiers = null;

        gameRunning = false;
        HandlerList.unregisterAll(this);
        countdown = getCountdown();
        cleanup();
    }

    protected abstract void startGame(@NotNull List<Player> players);

    protected void cleanup() {}
}
