package me.cable.crossover.main.inventoryitem;

import me.cable.crossover.main.handler.InventoryItems;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class InventoryItem {

    private final String id;

    public InventoryItem(@NotNull String id) {
        this.id = id;
    }

    public final void register() {
        InventoryItems.registerItemType(this);
    }

    public abstract @NotNull ItemStack createItem(@NotNull Player player);

    public void onClick(@NotNull InventoryClickEvent e) {}

    public final @NotNull String getId() {
        return id;
    }
}
