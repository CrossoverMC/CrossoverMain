package me.cable.crossover.main.object;

import me.cable.crossover.main.currency.CoinsCurrency;
import me.cable.crossover.main.currency.MoneyCurrency;
import me.cable.crossover.main.handler.ConfigHandler;
import me.cable.crossover.main.handler.InventoryItems;
import me.cable.crossover.main.handler.InventoryPlacers;
import me.cable.crossover.main.inventoryitem.*;
import me.cable.crossover.main.papi.CrossoverPE;
import me.cable.crossover.main.shop.CustomShopItem;
import me.cable.crossover.main.shop.ShopItem;
import me.cable.crossover.main.util.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class RegistryItems {

    private void registerCurrencies() {
        new CoinsCurrency().register();
        new MoneyCurrency().register();
    }

    private void registerItemTypes() {
        new HighblockBuffItem().register();
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

                boolean equipped = itemId.equals(equippedItem);
                ItemStack item = inventoryItem.createItem(player);
                item.setAmount(Math.min(amount, item.getType().getMaxStackSize()));

                List<String> extraLore = StringUtils.replace(ConfigHandler.settings()
                        .strList("inventory-items.extra-lore.amount"), Map.of(Utils.placeholder("amount"), Integer.toString(amount)));

                if (inventoryItem instanceof EquippableItem) {
                    extraLore.addAll(ConfigHandler.settings()
                            .strList("inventory-items.extra-lore." + (equipped ? "unequip" : "equip")));
                }

                ItemUtils.appendLore(item, StringUtils.format(extraLore), List.of(" "));
                ItemUtils.pd(item, Constants.KEY_PLAYER_ITEM, itemId);
                inv.setItem(equipped ? 7 : itemSlot++, item);

                if (itemSlot > 17) {
                    break;
                }
            }
        });
    }
}
