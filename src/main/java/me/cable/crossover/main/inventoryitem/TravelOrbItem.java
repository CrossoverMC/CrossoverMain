package me.cable.crossover.main.inventoryitem;

import me.cable.crossover.main.handler.ConfigHandler;
import me.cable.crossover.main.handler.InventoryItems;
import me.cable.crossover.main.handler.InventoryPlacers;
import me.cable.crossover.main.util.ConfigHelper;
import me.cable.crossover.main.util.ItemBuilder;
import me.cable.crossover.main.util.SoundEffect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TravelOrbItem extends EquippableItem {

    public static final String ID = "travel_orb";

    public TravelOrbItem() {
        super(ID);
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

        Player player = e.getPlayer();
        InventoryItems inventoryItems = InventoryItems.get(player);
        if (inventoryItems.get(ID) <= 0) return;

        ConfigHelper travelFrames = ConfigHandler.settings().ch("travel-frames");
        String blockLoc = block.getWorld().getName() + "," + block.getX() + "," + block.getY() + "," + block.getZ();

        for (String key : travelFrames.getKeys(false)) {
            if (key.equals(blockLoc)) {
                Location tpLoc = travelFrames.loc(key);

                if (tpLoc != null) {
                    player.teleport(tpLoc);
                    new SoundEffect(Sound.ENTITY_ENDERMAN_TELEPORT, 1.7f).play(player);

                    inventoryItems.remove(getId(), 1);
                    InventoryPlacers.place(player);
                }

                break;
            }
        }
    }
}
