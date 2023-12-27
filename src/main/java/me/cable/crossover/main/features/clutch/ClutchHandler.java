package me.cable.crossover.main.features.clutch;

import me.cable.crossover.main.util.ItemBuilder;
import me.cable.crossover.main.features.dohandler.DoModule;
import me.cable.crossover.main.util.Constants;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ClutchHandler implements DoModule {

    public static final String TYPE = "clutch";
    public static final int WATER_BUCKET_SLOT = Constants.PRIMARY_SLOT;

    public static final Map<Player, Integer> savedPlayerLevels = new HashMap<>();
    public static final Set<Player> clutchFailed = new HashSet<>();

    @Override
    public void onEnter(@NotNull Player player) {
        Inventory inventory = player.getInventory();
        inventory.setItem(WATER_BUCKET_SLOT, new ItemStack(Material.WATER_BUCKET));
        inventory.setItem(Constants.SECONDARY_SLOT, new ItemBuilder().material(Material.SOUL_TORCH)
                .name("&b&lLevel 1")
                .lore("&a&lRight-Click &7to go to this level.")
                .pd(Constants.TOOL_KEY, Constants.TOOL_LEVEL_1)
                .create());
        inventory.setItem(Constants.TERTIARY_SLOT, new ItemBuilder().material(Material.TORCH)
                .name("&6&lLevel 2")
                .lore("&a&lRight-Click &7to go to this level.")
                .pd(Constants.TOOL_KEY, Constants.TOOL_LEVEL_2)
                .create());
        inventory.setItem(Constants.QUATERNARY_SLOT, new ItemBuilder().material(Material.REDSTONE_TORCH)
                .name("&c&lLevel 3")
                .lore("&a&lRight-Click &7to go to this level.")
                .pd(Constants.TOOL_KEY, Constants.TOOL_LEVEL_3)
                .create());
    }

    @Override
    public void onLeave(@NotNull Player player) {
        Inventory inventory = player.getInventory();

        savedPlayerLevels.remove(player);
        clutchFailed.remove(player);

        for (int v : List.of(Constants.PRIMARY_SLOT, Constants.SECONDARY_SLOT, Constants.TERTIARY_SLOT, Constants.QUATERNARY_SLOT)) {
            inventory.setItem(v, null);
        }
    }
}
