package me.cable.crossover.main.util;

import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Supplier;

public class ItemBuilder {

    private @Nullable Supplier<ItemStack> itemCreator;
    private int amount = 1;
    private @Nullable String name;
    private final List<String> lore = new ArrayList<>();
    private @Nullable ConfigHelper config;

    private final Map<String, String> placeholders = new HashMap<>();
    private final Map<String, List<String>> lorePlaceholders = new HashMap<>();
    private final Map<NamespacedKey, String> stringPersistentData = new HashMap<>();

    public @NotNull ItemBuilder material(@NotNull Material material) {
        itemCreator = () -> new ItemStack(material);
        return this;
    }

    public @NotNull ItemBuilder hdb(int id) {
        itemCreator = () -> {
            ItemStack item = null;

            try {
                item = new HeadDatabaseAPI().getItemHead(Integer.toString(id)); // can return null during loading
            } catch (NullPointerException ex) {
                // ignored
            }

            return Objects.requireNonNullElse(item, new ItemStack(Material.PLAYER_HEAD));
        };

        return this;
    }

    public @NotNull ItemBuilder amount(int amount) {
        this.amount = amount;
        return this;
    }

    public @NotNull ItemBuilder name(@NotNull String name) {
        this.name = name;
        return this;
    }

    public @NotNull ItemBuilder lore(@NotNull List<String> lines) {
        lore.addAll(lines);
        return this;
    }

    public @NotNull ItemBuilder lore(@NotNull String... lines) {
        lore.addAll(Arrays.asList(lines));
        return this;
    }

    public @NotNull ItemBuilder placeholder(@NotNull String what, @NotNull String with) {
        placeholders.put(Utils.placeholder(what), with);
        return this;
    }

    public @NotNull ItemBuilder lorePlaceholder(@NotNull String what, @NotNull List<String> with) {
        lorePlaceholders.put(Utils.placeholder(what), with);
        return this;
    }

    public @NotNull ItemBuilder pd(@NotNull NamespacedKey namespacedKey, String data) {
        stringPersistentData.put(namespacedKey, data);
        return this;
    }

    public @NotNull ItemBuilder config(@NotNull ConfigHelper config) {
        this.config = config;

        Material mat = config.mat("material", null);
        if (mat != null) material(mat);

        String name = config.str("name");
        if (name != null) name(name);

        lore(config.strList("lore"));

        Integer am = config.integerIfSet("amount");
        if (am != null) amount = am;

        return this;
    }

    public @NotNull ItemBuilder config(@NotNull ConfigurationSection cs) {
        return config(new ConfigHelper(cs));
    }

    private @NotNull String format(@NotNull String str) {
        return StringUtils.format(StringUtils.replace(str, placeholders));
    }

    public @NotNull ItemStack create() {
        if (itemCreator == null) {
            throw new IllegalStateException("Material has not been specified");
        }

        ItemStack item = itemCreator.get();
        item.setAmount(amount);

        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            if (name != null) {
                meta.setDisplayName(format(name));
            }

            List<String> formattedLore = new ArrayList<>();

            t:
            for (String line : lore) {
                for (Entry<String, List<String>> entry : lorePlaceholders.entrySet()) {
                    if (line.equals(entry.getKey())) {
                        for (String s : entry.getValue()) {
                            formattedLore.add(format(s));
                        }

                        continue t;
                    }
                }

                formattedLore.add(format(line));
            }

            meta.setLore(formattedLore);

            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            stringPersistentData.forEach((key, val) -> pdc.set(key, PersistentDataType.STRING, val));

            item.setItemMeta(meta);
        }

        return item;
    }

    public void place(@NotNull Inventory inventory) {
        if (config == null) {
            throw new IllegalStateException("No config has been given");
        }

        ItemStack item = create();

        Integer slot = config.integerIfSet("slot");
        if (slot != null) inventory.setItem(slot, item);

        if (config.bool("fill")) {
            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, item);
            }
        }
        if (config.bool("outline")) {
            int size = inventory.getSize();

            // sides
            for (int i = 0; i < size; i++) {
                if (i % 9 == 0 || (i + 1) % 9 == 0) {
                    inventory.setItem(i, item);
                }
            }

            // top row
            for (int i = 1; i < 8; i++) {
                inventory.setItem(i, item);
            }

            // bottom row
            for (int i = size - 8; i < size - 1; i++) {
                inventory.setItem(i, item);
            }
        }
    }
}
