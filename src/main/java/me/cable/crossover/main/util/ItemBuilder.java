package me.cable.crossover.main.util;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ItemBuilder {

    private @Nullable Material material;
    private int amount = 1;
    private @Nullable String name;
    private @Nullable List<String> lore;
    private final Map<NamespacedKey, String> stringPersistentData = new HashMap<>();

    public @NotNull ItemStack create() {
        if (material == null) {
            throw new IllegalStateException("Material has not been specified");
        }

        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(StringUtils.format(name));

            if (lore != null) {
                List<String> formattedLore = new ArrayList<>();

                for (String line : lore) {
                    formattedLore.add(StringUtils.format(line));
                }

                meta.setLore(formattedLore);
            }

            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            stringPersistentData.forEach((key, val) -> pdc.set(key, PersistentDataType.STRING, val));

            item.setItemMeta(meta);
        }

        return item;
    }

    public @NotNull ItemBuilder material(@NotNull Material material) {
        this.material = material;
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
        this.lore = lines;
        return this;
    }

    public @NotNull ItemBuilder lore(@NotNull String... lines) {
        this.lore = Arrays.asList(lines);
        return this;
    }

    public @NotNull ItemBuilder pd(@NotNull NamespacedKey namespacedKey, String data) {
        stringPersistentData.put(namespacedKey, data);
        return this;
    }
}
