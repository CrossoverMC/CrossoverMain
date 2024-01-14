package me.cable.crossover.main.features.blockreplace;

import me.cable.crossover.main.CrossoverMain;
import me.cable.crossover.main.handler.ConfigHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockReplaceTask {

    private final CrossoverMain crossoverMain;
    private @Nullable Integer taskId;
    private boolean replacing;

    public BlockReplaceTask(@NotNull CrossoverMain crossoverMain) {
        this.crossoverMain = crossoverMain;
    }

    private boolean isEnabled() {
        return ConfigHandler.settings().bool("block-replace.enabled");
    }

    public void start() {
        if (taskId != null) {
            Bukkit.getScheduler().cancelTask(taskId);
        }

        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(crossoverMain, () -> {
            if (!isEnabled() || replacing) return;

            Location loc = ConfigHandler.settings().loc("block-replace.location");
            if (loc == null) return;

            Block block = loc.getBlock();
            Material mat = ConfigHandler.settings().mat("block-replace.material");

            if (block.getType() != mat) {
                replacing = true;
                Bukkit.getScheduler().scheduleSyncDelayedTask(crossoverMain, () -> {
                    if (isEnabled()) block.setType(mat);
                    replacing = false;
                }, ConfigHandler.settings().integer("block-replace.time"));
            }
        }, 0, 20);
    }
}
