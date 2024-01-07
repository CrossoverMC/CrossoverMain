package me.cable.crossover.main.inventoryitem;

import me.cable.crossover.main.CrossoverMain;
import me.cable.crossover.main.features.playerspeed.SpeedModifier;
import me.cable.crossover.main.handler.ConfigHandler;
import me.cable.crossover.main.handler.InventoryItems;
import me.cable.crossover.main.handler.InventoryPlacers;
import me.cable.crossover.main.handler.PlayerData;
import me.cable.crossover.main.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class SpeedBoostItem extends InventoryItem {

    public static final String ID = "speed_boost";

    private static final Map<SpeedModifier, Integer> speedModifiers = new HashMap<>(); // modifier, time

    public SpeedBoostItem() {
        super(ID);
        startTask();
    }

    private static boolean hasSpeedBoost(@NotNull Player player) {
        for (SpeedModifier speedModifier : speedModifiers.keySet()) {
            if (speedModifier.getPlayer().equals(player)) {
                return true;
            }
        }

        return false;
    }

    private static void addSpeedBoost(@NotNull Player player, int duration) {
        if (hasSpeedBoost(player)) return;

        float speed = (float) ConfigHandler.settings().doub("speed-boost.speed");
        SpeedModifier speedModifier = new SpeedModifier(player, speed);
        speedModifiers.put(speedModifier, duration);
        speedModifier.attachWalk();
    }

    public static void onPlayerJoin(@NotNull Player player) {
        YamlConfiguration playerData = PlayerData.get(player.getUniqueId());
        int duration = playerData.getInt(PlayerData.PATH_SPEED_BOOST);
        playerData.set(PlayerData.PATH_SPEED_BOOST, null);

        if (duration > 0) {
            addSpeedBoost(player, duration);
        }
    }

    public static void saveSpeedBoosts() {
        for (Entry<SpeedModifier, Integer> entry : Map.copyOf(speedModifiers).entrySet()) {
            Player player = entry.getKey().getPlayer();
            int duration = entry.getValue() - 1;

            YamlConfiguration playerData = PlayerData.get(player.getUniqueId());
            playerData.set(PlayerData.PATH_SPEED_BOOST, duration);
        }
    }

    private void startTask() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(CrossoverMain.getInstance(), () -> {
            for (Entry<SpeedModifier, Integer> entry : Map.copyOf(speedModifiers).entrySet()) {
                SpeedModifier speedModifier = entry.getKey();
                int duration = entry.getValue() - 1;
                Player player = speedModifier.getPlayer();

                if (duration <= 0) {
                    speedModifiers.remove(speedModifier);
                    speedModifier.detachWalk();
                    ConfigHandler.settings().message(ConfigHandler.PATH_MESSAGES + ".speed-boost-expire").send(player);
                } else if (!player.isOnline()) {
                    YamlConfiguration playerData = PlayerData.get(player.getUniqueId());
                    playerData.set(PlayerData.PATH_SPEED_BOOST, duration);
                    speedModifiers.remove(speedModifier);
                } else {
                    speedModifiers.put(speedModifier, duration);
                }
            }
        }, 0, 20);
    }

    @Override
    public @NotNull ItemStack createItem(@NotNull Player player) {
        return new ItemBuilder()
                .config(ConfigHandler.settings().csnn(ConfigHandler.PATH_INVENTORY_ITEMS + ".speed-boost"))
                .create();
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent e) {
        if (e.getClick() != ClickType.LEFT || !(e.getWhoClicked() instanceof Player player)) return;

        InventoryItems inventoryItems = InventoryItems.get(player);
        int amount = inventoryItems.get(ID);

        if (amount <= 0) {
            return;
        }
        if (hasSpeedBoost(player)) {
            ConfigHandler.settings().message(ConfigHandler.PATH_MESSAGES + ".speed-boost-already-active").send(player);
            return;
        }

        inventoryItems.remove(ID, 1);
        InventoryPlacers.place(player);

        int duration = ConfigHandler.settings().integer("speed-boost.duration");
        addSpeedBoost(player, duration);

        ConfigHandler.settings().message(ConfigHandler.PATH_MESSAGES + ".speed-boost-activate").send(player);

        if (ConfigHandler.settings().bool("speed-boost.activate-sound.enabled")) {
            ConfigHandler.settings().sound("speed-boost.activate-sound.sound").play(player);
        }
    }
}
