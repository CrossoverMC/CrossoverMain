package me.cable.crossover.main.inventoryitem;

import me.cable.crossover.main.handler.SettingsConfigHandler;
import me.cable.crossover.main.util.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SapphireItem extends EquippableItem {

    public SapphireItem() {
        super("sapphire");
    }

    @Override
    public @NotNull ItemStack createItem(@NotNull Player player) {
        return new ItemBuilder()
                .config(SettingsConfigHandler.getConfig().csnn(SettingsConfigHandler.PATH_INVENTORY_ITEMS + ".sapphire"))
                .create();
    }
}
