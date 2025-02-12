package me.cable.crossover.main.handler;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class InventoryPlacers {

    private static final List<Consumer<Player>> placers = new ArrayList<>();

    public static void register(@NotNull Consumer<Player> placer) {
        placers.add(placer);
    }

    public static void place(@NotNull Player player) {
        Inventory inv = player.getInventory();

        for (int i = 5; i < inv.getSize(); i++) {
            inv.setItem(i, null);
        }
        for (Consumer<Player> placer : placers) {
            placer.accept(player);
        }
    }
}
