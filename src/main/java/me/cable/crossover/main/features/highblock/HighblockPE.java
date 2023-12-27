package me.cable.crossover.main.features.highblock;

import me.cable.crossover.main.object.Minigame;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map.Entry;

public class HighblockPE extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "highblock";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Cable";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    private @Nullable HighblockMinigame getCurrent(@NotNull Player player) {
        for (Minigame minigame : Minigame.getRegisteredMinigames()) {
            if (minigame instanceof HighblockMinigame highblockMinigame && highblockMinigame.isInGame(player.getLocation())) {
                return highblockMinigame;
            }
        }

        return null;
    }

    @Override
    public @Nullable String onPlaceholderRequest(@Nullable Player player, @NotNull String params) {
        if (player == null) return null;

        HighblockMinigame highblockMinigame = getCurrent(player);
        if (highblockMinigame == null) return null;

        String[] args = params.split("_");

        switch (args[0]) {
            case "players", "scores" -> {
                if (args.length < 2) return null;

                int pos;

                try {
                    pos = Integer.parseInt(args[1]) - 1;
                } catch (NumberFormatException ex) {
                    return null;
                }

                List<Entry<Player, Integer>> topScores = highblockMinigame.getTopScores();

                if (topScores.size() <= pos) {
                    return args[0].equals("players") ? "None" : "00";
                }

                Entry<Player, Integer> entry = topScores.get(pos);

                if (args[0].equals("players")) {
                    return entry.getKey().getName();
                } else {
                    int score = entry.getValue();
                    return String.format("%02d", score);
                }
            }
            case "time" -> {
                int totalSeconds = highblockMinigame.getRemainingSeconds();
                int minutes = totalSeconds / 60;
                int seconds = totalSeconds % 60;
                return String.format("%02d:%02d", minutes, seconds);
            }
        }

        return null;
    }
}
