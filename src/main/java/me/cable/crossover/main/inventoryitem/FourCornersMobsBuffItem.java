package me.cable.crossover.main.inventoryitem;

import me.cable.crossover.main.handler.ConfigHandler;
import me.cable.crossover.main.util.Color;
import me.cable.crossover.main.util.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class FourCornersMobsBuffItem extends InventoryItem {

    public FourCornersMobsBuffItem() {
        super("four_corners_mobs_buff");
    }

    @Override
    public @NotNull ItemStack createItem(@NotNull Player player) {
        return new ItemBuilder()
                .config(ConfigHandler.settings().csnn(ConfigHandler.PATH_INVENTORY_ITEMS + ".four-corners-mobs-buff"))
                .create();
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent e) {
        if (e.getClick() != ClickType.LEFT || !(e.getWhoClicked() instanceof Player player)) return;

        player.sendMessage(Color.SPECIAL + "This feature is coming soon!");
    }
}
