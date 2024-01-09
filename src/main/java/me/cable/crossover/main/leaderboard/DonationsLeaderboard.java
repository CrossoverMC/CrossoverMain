package me.cable.crossover.main.leaderboard;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class DonationsLeaderboard extends Leaderboard {

    public DonationsLeaderboard() {
        super("donations");
    }

    @Override
    protected @NotNull List<Position> getPositions() {
        return Collections.emptyList();
    }
}
