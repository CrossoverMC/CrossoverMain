package me.cable.crossover.main.handler;

import me.cable.crossover.main.playeritem.ItemType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class PlayerItems {

    private static final String PATH_ITEMS_EQUIPPED = "items.equipped";
    private static final String PATH_ITEMS_ITEMS = "items.items";
    private static final Map<String, ItemType> itemTypes = new HashMap<>();

    private final ConfigurationSection itemsCs;

    public static void registerItemType(@NotNull ItemType itemType) {
        String id = itemType.getId();

        if (itemTypes.containsKey(id)) {
            throw new IllegalStateException("An item type with the ID " + id + " has already been registered");
        }

        itemTypes.put(id, itemType);
    }

    public static @NotNull List<String> getItemTypes() {
        return new ArrayList<>(itemTypes.keySet());
    }

    public static boolean isValidItem(@NotNull String itemId) {
        return itemTypes.containsKey(itemId);
    }

    private static void checkItemType(@NotNull String itemId) {
        if (!isValidItem(itemId)) {
            throw new IllegalArgumentException("Invalid item type " + itemId);
        }
    }

    public static @NotNull PlayerItems getPlayerItems(@NotNull Player player) {
        return new PlayerItems(PlayerData.get(player.getUniqueId()));
    }

    public static @Nullable ItemType getItemType(@NotNull String itemType) {
        return itemTypes.get(itemType);
    }

    public PlayerItems(@NotNull ConfigurationSection itemsCs) {
        this.itemsCs = itemsCs;
    }

    public @NotNull Map<String, Integer> get() {
        Map<String, Integer> items = new HashMap<>();

        for (Entry<String, ItemType> entry : itemTypes.entrySet()) {
            String itemId = entry.getKey();
            int amount = get(itemId);

            if (amount > 0) {
                items.put(itemId, amount);
            }
        }

        return items;
    }

    public int get(@NotNull String itemId) {
        checkItemType(itemId);
        return itemsCs.getInt(PATH_ITEMS_ITEMS + "." + itemId);
    }

    public void set(@NotNull String itemId, int amount) {
        itemsCs.set(PATH_ITEMS_ITEMS + "." + itemId, amount);
    }

    public void give(@NotNull String itemId, int amount) {
        checkItemType(itemId);

        int currentAmount = itemsCs.getInt(PATH_ITEMS_ITEMS + "." + itemId);
        itemsCs.set(PATH_ITEMS_ITEMS + "." + itemId, currentAmount + amount);
    }

    public void remove(@NotNull String itemId, int amount) {
        checkItemType(itemId);
        int newAmount = itemsCs.getInt(PATH_ITEMS_ITEMS + "." + itemId) - amount;
        itemsCs.set(PATH_ITEMS_ITEMS + "." + itemId, (newAmount > 0) ? newAmount : null);
    }

    public @Nullable String getEquipped() {
        return itemsCs.getString(PATH_ITEMS_EQUIPPED);
    }

    public void setEquipped(@Nullable String equipped) {
        itemsCs.set(PATH_ITEMS_EQUIPPED, equipped);
    }
}
