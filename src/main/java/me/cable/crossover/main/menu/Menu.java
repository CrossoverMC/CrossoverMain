package me.cable.crossover.main.menu;

import me.cable.crossover.main.CrossoverMain;
import me.cable.crossover.main.util.ItemBuilder;
import me.cable.crossover.main.util.StringUtils;
import me.cable.crossover.main.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class Menu implements InventoryHolder {

    private static final NamespacedKey ITEM_KEY = new NamespacedKey(CrossoverMain.getInstance(), "menu");

    private final Map<String, Supplier<String>> titlePlaceholders = new HashMap<>();
    private final Map<RenderPriority, List<Consumer<Inventory>>> renderers = new HashMap<>();
    private final List<BiConsumer<InventoryClickEvent, String>> onClickListeners = new ArrayList<>(); // event, tag
    private @Nullable Inventory inventory;
    protected final Player player;

    protected Menu(@NotNull Player player) {
        this.player = player;
    }

    public final void open() {
        if (inventory == null || forceRecreate()) {
            inventory = getInventory();
        } else {
            inventory.clear();
        }

        for (RenderPriority renderPriority : RenderPriority.values()) {
            List<Consumer<Inventory>> r = renderers.get(renderPriority);

            if (r != null) {
                r.forEach(c -> c.accept(inventory));
            }
        }

        player.openInventory(inventory);
    }

    protected final void tag(@NotNull ItemStack item, @NotNull String value) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        meta.getPersistentDataContainer().set(ITEM_KEY, PersistentDataType.STRING, value);
        item.setItemMeta(meta);
    }

    protected boolean forceRecreate() {
        return false;
    }

    protected abstract @NotNull String title();

    protected abstract int rows();

    @Override
    public final @NotNull Inventory getInventory() {
        String title = title();

        for (Entry<String, Supplier<String>> entry : titlePlaceholders.entrySet()) {
            title = title.replace(entry.getKey(), entry.getValue().get());
        }

        return Bukkit.createInventory(this, rows() * 9, StringUtils.format(title));
    }

    protected final void titlePlaceholder(@NotNull String what, @NotNull Supplier<String> with) {
        titlePlaceholders.put(Utils.placeholder(what), with);
    }

    protected final void render(@NotNull RenderPriority renderPriority, @NotNull Consumer<Inventory> c) {
        renderers.computeIfAbsent(renderPriority, r -> new ArrayList<>()).add(c);
    }

    protected final void render(@NotNull Consumer<Inventory> c) {
        render(RenderPriority.NORMAL, c);
    }

    protected final void handleCustomItems(@NotNull ConfigurationSection cs) {
        render(RenderPriority.LOWEST, inv -> {
            for (String key : cs.getKeys(false)) {
                ConfigurationSection itemSection = cs.getConfigurationSection(key);

                if (itemSection != null) {
                    new ItemBuilder().config(itemSection).place(inv);
                }
            }
        });
    }

    protected final void onClick(@NotNull BiConsumer<InventoryClickEvent, String> c) {
        onClickListeners.add(c);
    }

    public final void onClickEvent(@NotNull InventoryClickEvent e) {
        ItemStack item = e.getCurrentItem();
        String tag = null;

        if (item != null) {
            ItemMeta meta = item.getItemMeta();

            if (meta != null) {
                tag = meta.getPersistentDataContainer().get(ITEM_KEY, PersistentDataType.STRING);
            }
        }

        e.setCancelled(true);

        for (BiConsumer<InventoryClickEvent, String> c : onClickListeners) {
            c.accept(e, tag);
        }
    }

    public enum RenderPriority {
        LOWEST,
        LOW,
        NORMAL,
        HIGH,
        HIGHEST
    }
}
