package me.cable.crossover.main.menu;

import me.cable.crossover.main.CrossoverMain;
import me.cable.crossover.main.util.ItemBuilder;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class PagedMenu extends Menu {

    private static final NamespacedKey ITEM_KEY = new NamespacedKey(CrossoverMain.getInstance(), "paged-menu");

    private final List<ItemStack> items;
    private final List<Integer> itemSlots;

    private int page = 0;

    protected PagedMenu(@NotNull Player player) {
        super(player);
        items = List.copyOf(items());
        itemSlots = List.copyOf(itemSlots());

        titlePlaceholder("page", () -> Integer.toString(page + 1));
        titlePlaceholder("pages", () -> Integer.toString(getTotalPages()));

        render(inv -> {
            int itemsPerPage = itemSlots.size();

            for (int i = 0; i < itemsPerPage; i++) {
                int itemI = itemsPerPage * page + i;
                if (itemI >= items.size()) break;

                ItemStack item = items.get(itemI);
                inv.setItem(itemSlots.get(i), item);
            }

            if (page < getTotalPages() - 1) {
                nextItem().pd(ITEM_KEY, "NEXT").place(inv);
            }
            if (page > 0) {
                previousItem().pd(ITEM_KEY, "PREVIOUS").place(inv);
            }
        });

        onClick((e, t) -> {
            ItemStack item = e.getCurrentItem();
            if (item == null) return;

            ItemMeta meta = item.getItemMeta();
            if (meta == null) return;

            String tag = meta.getPersistentDataContainer().get(ITEM_KEY, PersistentDataType.STRING);
            if (tag == null) return;

            switch (tag) {
                case "NEXT" -> {
                    page++;
                    open();
                }
                case "PREVIOUS" -> {
                    page--;
                    open();
                }
            }
        });
    }

    protected abstract @NotNull List<ItemStack> items();

    protected abstract @NotNull List<Integer> itemSlots();

    protected abstract @NotNull ItemBuilder nextItem();

    protected abstract @NotNull ItemBuilder previousItem();

    @Override
    protected boolean forceRecreate() {
        return true; // update page title placeholders
    }

    private int getTotalPages() {
        int itemsPerPage = itemSlots.size();
        int total = (int) Math.ceil((double) items.size() / itemsPerPage);
        return Math.max(total, 1);
    }
}
