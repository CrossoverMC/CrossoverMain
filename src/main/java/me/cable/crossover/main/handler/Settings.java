package me.cable.crossover.main.handler;

import me.cable.crossover.main.util.ConfigHelper;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

@Deprecated // make new system
public class Settings extends ConfigHelper {

    public Settings(@NotNull ConfigurationSection cs) {
        super(cs);
    }

    public @NotNull Location clutchLevels_level(@NotNull World world, int level) {
        return loc("clutch-levels." + level, world);
    }

    public @NotNull ConfigurationSection velocityBlocks() {
        return csnn("velocity-blocks");
    }
}
