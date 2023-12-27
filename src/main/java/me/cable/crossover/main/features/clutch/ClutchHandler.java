package me.cable.crossover.main.features.clutch;

import me.cable.crossover.main.util.ItemBuilder;
import me.cable.crossover.main.features.dohandler.DoModule;
import me.cable.crossover.main.util.Keys;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ClutchHandler implements DoModule {

    public static final String TYPE = "clutch";
    public static final int WATER_BUCKET_SLOT = Keys.PRIMARY_SLOT;

    public static final Map<Player, Integer> savedPlayerLevels = new HashMap<>();
    public static final Set<Player> clutchFailed = new HashSet<>();

    @Override
    public void onEnter(@NotNull Player player) {
        Inventory inventory = player.getInventory();
        inventory.setItem(WATER_BUCKET_SLOT, new ItemStack(Material.WATER_BUCKET));
        inventory.setItem(Keys.SECONDARY_SLOT, new ItemBuilder().material(Material.SOUL_TORCH)
                .name("&b&lLevel 1")
                .lore("&a&lRight-Click &7to go to this level.")
                .pd(Keys.TOOL, Keys.TOOL_LEVEL_1)
                .create());
        inventory.setItem(Keys.TERTIARY_SLOT, new ItemBuilder().material(Material.TORCH)
                .name("&6&lLevel 2")
                .lore("&a&lRight-Click &7to go to this level.")
                .pd(Keys.TOOL, Keys.TOOL_LEVEL_2)
                .create());
        inventory.setItem(Keys.QUATERNARY_SLOT, new ItemBuilder().material(Material.REDSTONE_TORCH)
                .name("&c&lLevel 3")
                .lore("&a&lRight-Click &7to go to this level.")
                .pd(Keys.TOOL, Keys.TOOL_LEVEL_3)
                .create());
    }

    @Override
    public void onLeave(@NotNull Player player) {
        Inventory inventory = player.getInventory();

        savedPlayerLevels.remove(player);
        clutchFailed.remove(player);

        for (int v : List.of(Keys.PRIMARY_SLOT, Keys.SECONDARY_SLOT, Keys.TERTIARY_SLOT, Keys.QUATERNARY_SLOT)) {
            inventory.setItem(v, null);
        }
    }
}
