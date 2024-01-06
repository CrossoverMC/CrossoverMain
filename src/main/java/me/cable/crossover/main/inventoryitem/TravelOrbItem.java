package me.cable.crossover.main.inventoryitem;

import me.cable.crossover.main.handler.InventoryItems;
import me.cable.crossover.main.handler.InventoryPlacers;
import me.cable.crossover.main.handler.ConfigHandler;
import me.cable.crossover.main.util.ConfigHelper;
import me.cable.crossover.main.util.ItemBuilder;
import me.cable.crossover.main.util.Utils;
import org.bukkit.Location;
import org.bukkit.Sound;
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
                .config(ConfigHandler.settings().csnn(ConfigHandler.PATH_INVENTORY_ITEMS + ".travel-orb"))
                .create();
    }

    @Override
    public void onInteract(@NotNull PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block block = e.getClickedBlock();
        if (block == null) return;

        ConfigHelper travelFrames = ConfigHandler.settings().ch("travel-frames");
        String blockLoc = block.getWorld().getName() + "," + block.getX() + "," + block.getY() + "," + block.getZ();

        for (String key : travelFrames.getKeys(false)) {
            if (key.equals(blockLoc)) {
                Location tpLoc = travelFrames.loc(key);

                if (tpLoc != null) {
                    Player player = e.getPlayer();
                    player.teleport(tpLoc);
                    Utils.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1.7f);

                    InventoryItems.get(player).remove(getId(), 1);
                    InventoryPlacers.place(player);
                }

                break;
            }
        }
    }
}
