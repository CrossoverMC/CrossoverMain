package me.cable.crossover.main.task;

import me.cable.crossover.main.leaderboard.Leaderboard;

public class UpdateLeaderboardTask implements Runnable {

    @Override
    public void run() {
        Leaderboard.updateLeaderboards();
    }
}
