package me.cable.crossover.main.util;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SoundEffect {

    public static final SoundEffect SUCCESS = new SoundEffect(Sound.ENTITY_EXPERIENCE_ORB_PICKUP);

    private final Sound sound;
    private final float pitch;

    public static @NotNull SoundEffect of(@NotNull String str) {
        String[] parts = str.split(",");

        try {
            Sound sound = Sound.valueOf(parts[0]);
            float pitch = Float.parseFloat(parts[1]);
            return new SoundEffect(sound, pitch);
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException ex) {
            return SoundEffect.SUCCESS;
        }
    }

    public SoundEffect(@NotNull Sound sound, float pitch) {
        this.sound = sound;
        this.pitch = pitch;
    }

    public SoundEffect(@NotNull Sound sound) {
        this(sound, 1);
    }

    public void play(@NotNull Player player, float volume) {
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    public void play(@NotNull Player player) {
        play(player, Float.MAX_VALUE);
    }
}
