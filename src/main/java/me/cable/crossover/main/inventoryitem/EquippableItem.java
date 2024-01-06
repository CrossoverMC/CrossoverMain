package me.cable.crossover.main.inventoryitem;

import me.cable.crossover.main.handler.InventoryPlacers;
import me.cable.crossover.main.handler.InventoryItems;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

public abstract class EquippableItem extends InventoryItem {

    public EquippableItem(@NotNull String id) {
        super(id);
    }

    @Override
    public final void onClick(@NotNull InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player) || e.getClick() != ClickType.LEFT) return;

        InventoryItems inventoryItems = InventoryItems.get(player);
        String itemId = getId();

        inventoryItems.setEquipped(itemId.equals(inventoryItems.getEquipped()) ? null : itemId);
        InventoryPlacers.place(player);
    }

    public void onInteract(@NotNull PlayerInteractEvent e) {}
}
