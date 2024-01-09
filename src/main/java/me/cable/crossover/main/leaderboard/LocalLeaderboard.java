package me.cable.crossover.main.leaderboard;

import me.cable.crossover.main.handler.LeaderboardsConfigHandler;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class LocalLeaderboard extends Leaderboard {

    public LocalLeaderboard(@NotNull String id) {
        super(id);
    }

    protected abstract @NotNull String format(int value);

    protected abstract @NotNull List<IntPosition> sort(@NotNull List<IntPosition> positions);

    @Override
    protected final @NotNull List<Position> getPositions() {
        List<IntPosition> list = new ArrayList<>();
        ConfigurationSection playerData = LeaderboardsConfigHandler.config().getConfigurationSection(getId());

        if (playerData != null) {
            for (String key : playerData.getKeys(false)) {
                UUID playerUuid = UUID.fromString(key);
                int value = playerData.getInt(key);
                list.add(new IntPosition(playerUuid, value));
            }
        }

        List<Position> positions = new ArrayList<>();

        for (IntPosition intPosition : sort(list)) {
            Position position = new Position(intPosition.playerUuid(), format(intPosition.value()));
            positions.add(position);
        }

        return positions;
    }

    public record IntPosition(@NotNull UUID playerUuid, int value) {}
}
