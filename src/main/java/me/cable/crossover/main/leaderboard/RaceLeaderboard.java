package me.cable.crossover.main.leaderboard;

import me.cable.crossover.main.util.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;

public class RaceLeaderboard extends LocalLeaderboard {

    public RaceLeaderboard() {
        super("race");
    }

    @Override
    protected @NotNull String format(int value) {
        return Utils.formatDurationMillis(value);
    }

    @Override
    protected @NotNull List<IntPosition> sort(@NotNull List<IntPosition> positions) {
        positions.sort(Comparator.comparingInt(IntPosition::value));
        return positions;
    }
}
