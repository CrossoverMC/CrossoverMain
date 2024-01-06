package me.cable.crossover.main.playeritem;

import me.cable.crossover.main.handler.InventoryPlacers;
import me.cable.crossover.main.handler.PlayerItems;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

public abstract class EquippableItem extends ItemType {

    public EquippableItem(@NotNull String id) {
        super(id);
    }

    @Override
    public final void onClick(@NotNull InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player) || e.getClick() != ClickType.LEFT) return;

        PlayerItems playerItems = PlayerItems.getPlayerItems(player);
        String itemId = getId();

        playerItems.setEquipped(itemId.equals(playerItems.getEquipped()) ? null : itemId);
        InventoryPlacers.place(player);
    }

    public void onInteract(@NotNull PlayerInteractEvent e) {}
}
