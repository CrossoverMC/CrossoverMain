package me.cable.crossover.main.playeritem;

import me.cable.crossover.main.handler.SettingsConfigHandler;
import me.cable.crossover.main.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TravelOrbItem extends EquippableItem {

    public TravelOrbItem() {
        super("travel-orb");
    }

    @Override
    public @NotNull ItemStack createItem(@NotNull Player player) {
        return new ItemBuilder()
                .config(SettingsConfigHandler.getConfig().csnn(SettingsConfigHandler.PATH_PLAYER_ITEMS + ".travel-orb"))
                .create();
    }

    @Override
    public void onInteract(@NotNull PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block block = e.getClickedBlock();
        if (block == null) return;

        if (block.getType() == Material.END_PORTAL_FRAME) {
            Bukkit.broadcastMessage("found end portal frame");
        }
    }
}
