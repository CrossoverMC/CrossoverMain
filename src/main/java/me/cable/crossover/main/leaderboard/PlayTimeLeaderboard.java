package me.cable.crossover.main.leaderboard;

import me.cable.crossover.main.handler.LeaderboardsConfigHandler;
import me.cable.crossover.main.util.Utils;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

public class PlayTimeLeaderboard extends Leaderboard {

    public PlayTimeLeaderboard() {
        super("playtime");
    }

    @Override
    protected @NotNull List<Position> getPositions() {
        List<Entry<UUID, Integer>> times = new ArrayList<>();
        ConfigurationSection cs = LeaderboardsConfigHandler.config().getConfigurationSection(LeaderboardsConfigHandler.PATH_PLAYTIME);

        if (cs != null) {
            for (String key : cs.getKeys(false)) {
                UUID playerUuid = UUID.fromString(key);
                int time = cs.getInt(key);
                times.add(new AbstractMap.SimpleEntry<>(playerUuid, time));
            }
        }

        return times.stream().sorted((a, b) -> b.getValue() - a.getValue())
                .map((a) -> new Position(a.getKey(), Utils.formatDurationSeconds(a.getValue()))).toList();
    }
}
