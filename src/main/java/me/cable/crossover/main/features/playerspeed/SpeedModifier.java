package me.cable.crossover.main.features.playerspeed;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class SpeedModifier {

    private static final List<SpeedModifier> flySpeedModifiers = new ArrayList<>();
    private static final List<SpeedModifier> walkSpeedModifiers = new ArrayList<>();

    private final Player player;
    private final float value;
    private final SpeedPriority speedPriority;

    public static void removePlayerModifiers(@NotNull Player player) {
        Predicate<SpeedModifier> predicate = s -> s.getPlayer().equals(player);
        flySpeedModifiers.removeIf(predicate);
        walkSpeedModifiers.removeIf(predicate);
    }

    private static void updateSpeed(@NotNull Player player, boolean walk) {
        List<SpeedModifier> playerModifiers = (walk ? walkSpeedModifiers : flySpeedModifiers).stream()
                .filter(m -> m.getPlayer().equals(player))
                .sorted((a, b) -> b.getPriority().ordinal() - a.getPriority().ordinal())
                .toList();
        float value = playerModifiers.isEmpty() ? (walk ? 0.2f : 0.1f) : playerModifiers.get(0).getValue();

        if (walk) {
            player.setWalkSpeed(value);
        } else {
            player.setFlySpeed(value);
        }
    }

    public SpeedModifier(@NotNull Player player, float value, @NotNull SpeedPriority speedPriority) {
        this.player = player;
        this.value = value;
        this.speedPriority = speedPriority;
    }

    public SpeedModifier(@NotNull Player player, float value) {
        this(player, value, SpeedPriority.NORMAL);
    }

    public void attachFly() {
        if (!flySpeedModifiers.contains(this)) {
            flySpeedModifiers.add(this);
            updateSpeed(player, false);
        }
    }

    public void detachFly() {
        if (flySpeedModifiers.contains(this)) {
            flySpeedModifiers.remove(this);
            updateSpeed(player, false);
        }
    }

    public void attachWalk() {
        if (!walkSpeedModifiers.contains(this)) {
            walkSpeedModifiers.add(this);
            updateSpeed(player, true);
        }
    }

    public void detachWalk() {
        if (walkSpeedModifiers.contains(this)) {
            walkSpeedModifiers.remove(this);
            updateSpeed(player, true);
        }
    }

    public @NotNull Player getPlayer() {
        return player;
    }

    public float getValue() {
        return value;
    }

    public @NotNull SpeedPriority getPriority() {
        return speedPriority;
    }
}
