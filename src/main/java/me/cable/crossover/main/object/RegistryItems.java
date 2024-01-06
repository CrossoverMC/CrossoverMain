package me.cable.crossover.main.object;

import me.cable.crossover.main.currency.CoinsCurrency;
import me.cable.crossover.main.currency.MoneyCurrency;
import me.cable.crossover.main.handler.InventoryItems;
import me.cable.crossover.main.handler.InventoryPlacers;
import me.cable.crossover.main.handler.ConfigHandler;
import me.cable.crossover.main.inventoryitem.InventoryItem;
import me.cable.crossover.main.inventoryitem.SapphireItem;
import me.cable.crossover.main.inventoryitem.TravelOrbItem;
import me.cable.crossover.main.papi.CrossoverPE;
import me.cable.crossover.main.shop.CustomShopItem;
import me.cable.crossover.main.shop.ShopItem;
import me.cable.crossover.main.util.Constants;
import me.cable.crossover.main.util.ItemBuilder;
import me.cable.crossover.main.util.ItemUtils;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map.Entry;

public class RegistryItems {

    private void registerCurrencies() {
        new CoinsCurrency().register();
        new MoneyCurrency().register();
    }

    private void registerItemTypes() {
        new SapphireItem().register();
        new TravelOrbItem().register();
    }

    private void registerPlaceholderExpansions() {
        new CrossoverPE().register();
    }

    private void registerShopItems() {
        ShopItem.register("custom", CustomShopItem::new);
    }

    public void register() {
        registerCurrencies();
        registerItemTypes();
        registerPlaceholderExpansions();
        registerShopItems();

        InventoryPlacers.register(player -> {
            Inventory inv = player.getInventory();
            ItemStack menuItem = new ItemBuilder().config(ConfigHandler.settings().csnn("menu-item"))
                    .pd(Constants.KEY_TOOL, Constants.TOOL_MAIN_MENU)
                    .create();
            inv.setItem(8, menuItem);

            InventoryItems inventoryItems = InventoryItems.get(player);
            String equippedItem = inventoryItems.getEquipped();
            int itemSlot = 9;

            for (Entry<String, Integer> entry : inventoryItems.get().entrySet()) {
                String itemId = entry.getKey();
                int amount = entry.getValue();

                InventoryItem inventoryItem = InventoryItems.getItemType(itemId);
                if (inventoryItem == null) continue;

                ItemStack item = inventoryItem.createItem(player);
                item.setAmount(Math.min(amount, item.getType().getMaxStackSize()));
                ItemUtils.pd(item, Constants.KEY_PLAYER_ITEM, itemId);
                inv.setItem(itemId.equals(equippedItem) ? 7 : itemSlot++, item);

                if (itemSlot > 17) {
                    break;
                }
            }
        });
    }
}
