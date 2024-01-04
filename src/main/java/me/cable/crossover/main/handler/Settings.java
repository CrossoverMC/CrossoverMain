package me.cable.crossover.main.handler;

import me.cable.crossover.main.util.ConfigHelper;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class Settings extends ConfigHelper {

    public Settings(@NotNull ConfigurationSection cs) {
        super(cs);
    }

    public @NotNull Location clutchLevels_level(@NotNull World world, int level) {
        return loc("clutch-levels." + level, world);
    }

    public boolean fallTeleport_enabled() {
        return bool("fall-teleport.enabled");
    }

    public int fallTeleport_yFrom() {
        return integer("fall-teleport.y-from");
    }

    public int fallTeleport_yTo() {
        return integer("fall-teleport.y-to");
    }

    public @NotNull ConfigurationSection velocityBlocks() {
        return csnn("velocity-blocks");
    }
}
