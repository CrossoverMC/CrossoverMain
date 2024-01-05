package me.cable.crossover.main.task;

import me.cable.crossover.main.handler.SettingsConfigHandler;
import me.cable.crossover.main.util.Constants;
import me.cable.crossover.main.util.ItemBuilder;
import me.cable.crossover.main.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class InventoryItemsTask implements Runnable {

    private static final List<Consumer<Player>> placers = new ArrayList<>();

    public static void registerPlacer(@NotNull Consumer<Player> placer) {
        placers.add(placer);
    }

    public InventoryItemsTask() {
        registerPlacer(player -> {
            ItemStack item = new ItemBuilder().config(SettingsConfigHandler.getConfig().csnn("menu-item"))
                    .pd(Constants.TOOL_KEY, Constants.TOOL_MAIN_MENU)
                    .create();
            player.getInventory().setItem(8, item);
        });
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!Utils.hasBypass(player)) {
                placers.forEach(placer -> placer.accept(player));
            }
        }
    }
}
