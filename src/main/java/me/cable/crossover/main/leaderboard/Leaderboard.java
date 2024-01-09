package me.cable.crossover.main.leaderboard;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import me.cable.crossover.main.handler.ConfigHandler;
import me.cable.crossover.main.util.StringUtils;
import me.cable.crossover.main.util.Utils;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class Leaderboard {

    private static final List<Leaderboard> placedLeaderboards = new ArrayList<>();

    private final String id;
    private @Nullable Hologram hologram;

    public Leaderboard(@NotNull String id) {
        this.id = id;

        Location loc = ConfigHandler.settings().loc(ConfigHandler.PATH_LEADERBOARDS + ".leaderboards." + id + ".location");

        if (loc != null) {
            hologram = DHAPI.createHologram(id, loc);
            update();
        }
    }

    public static void updateLeaderboards() {
        placedLeaderboards.forEach(Leaderboard::update);
    }

    public static void loadLeaderboards() {
        placedLeaderboards.forEach(Leaderboard::remove);
        placedLeaderboards.clear();

        placedLeaderboards.addAll(List.of(
                new DonationsLeaderboard(),
                new HiddenPathLeaderboard(),
                new PlayTimeLeaderboard()
        ));
    }

    public void update() {
        if (hologram == null) return;

        List<Position> positions = getPositions();
        List<String> lines = new ArrayList<>();

        String title = ConfigHandler.settings().snn(ConfigHandler.PATH_LEADERBOARDS + ".leaderboards." + id + ".title");
        String format = ConfigHandler.settings().snn(ConfigHandler.PATH_LEADERBOARDS + ".format");
        int perPage = ConfigHandler.settings().integer(ConfigHandler.PATH_LEADERBOARDS + ".per-page");
        List<String> exclude = ConfigHandler.settings().strList(ConfigHandler.PATH_LEADERBOARDS + ".leaderboards." + id + ".exclude");

        lines.add(title);

        int pos = 1;

        for (Position position : positions) {
            UUID playerUuid = position.playerUuid();
            if (exclude.contains(playerUuid.toString())) continue;

            String name = Utils.playerNameFromUuid(playerUuid);
            if (name == null) name = "N/A";

            String line = StringUtils.replace(format, Map.of(
                    Utils.placeholder("player"), name,
                    Utils.placeholder("pos"), Integer.toString(pos),
                    Utils.placeholder("score"), position.value()
            ));

            lines.add(line);

            if (pos++ >= perPage) {
                break;
            }
        }

        DHAPI.setHologramLines(hologram, lines);
    }

    public void remove() {
        DHAPI.removeHologram(id);
    }

    protected abstract @NotNull List<Position> getPositions();

    public final @NotNull String getId() {
        return id;
    }

    public record Position(@NotNull UUID playerUuid, @NotNull String value) {}
}
