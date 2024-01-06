package me.cable.crossover.main.inventoryitem;

import me.cable.crossover.main.handler.ConfigHandler;
import me.cable.crossover.main.util.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SapphireItem extends InventoryItem {

    public static final String ID = "sapphire";

    public SapphireItem() {
        super(ID);
    }

    @Override
    public @NotNull ItemStack createItem(@NotNull Player player) {
        return new ItemBuilder()
                .config(ConfigHandler.settings().csnn(ConfigHandler.PATH_INVENTORY_ITEMS + ".sapphire"))
                .create();
    }
}
