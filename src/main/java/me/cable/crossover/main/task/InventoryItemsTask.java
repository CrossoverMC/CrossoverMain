package me.cable.crossover.main.task;

import me.cable.crossover.main.handler.InventoryPlacers;
import me.cable.crossover.main.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class InventoryItemsTask implements Runnable {

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!Utils.hasBypass(player)) {
                InventoryPlacers.place(player);
            }
        }
    }
}
