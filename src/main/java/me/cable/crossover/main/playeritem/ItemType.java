package me.cable.crossover.main.playeritem;

import me.cable.crossover.main.handler.PlayerItems;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class ItemType {

    private final String id;

    public ItemType(@NotNull String id) {
        this.id = id;
    }

    public final void register() {
        PlayerItems.registerItemType(this);
    }

    public abstract @NotNull ItemStack createItem(@NotNull Player player);

    public void onClick(@NotNull InventoryClickEvent e) {}

    public final @NotNull String getId() {
        return id;
    }
}
