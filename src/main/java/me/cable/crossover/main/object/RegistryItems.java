package me.cable.crossover.main.object;

import me.cable.crossover.main.currency.CoinsCurrency;
import me.cable.crossover.main.currency.MoneyCurrency;
import me.cable.crossover.main.handler.InventoryPlacers;
import me.cable.crossover.main.handler.PlayerItems;
import me.cable.crossover.main.handler.SettingsConfigHandler;
import me.cable.crossover.main.papi.CrossoverPE;
import me.cable.crossover.main.playeritem.ItemType;
import me.cable.crossover.main.playeritem.SapphireItem;
import me.cable.crossover.main.playeritem.TravelOrbItem;
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
            ItemStack menuItem = new ItemBuilder().config(SettingsConfigHandler.getConfig().csnn("menu-item"))
                    .pd(Constants.KEY_TOOL, Constants.TOOL_MAIN_MENU)
                    .create();
            inv.setItem(8, menuItem);

            PlayerItems playerItems = PlayerItems.getPlayerItems(player);
            String equippedItem = playerItems.getEquipped();
            int itemSlot = 9;

            for (Entry<String, Integer> entry : playerItems.get().entrySet()) {
                String itemId = entry.getKey();
                int amount = entry.getValue();

                ItemType itemType = PlayerItems.getItemType(itemId);
                if (itemType == null) continue;

                ItemStack item = itemType.createItem(player);
                item.setAmount(amount);
                ItemUtils.pd(item, Constants.KEY_PLAYER_ITEM, itemId);
                inv.setItem(itemId.equals(equippedItem) ? 7 : itemSlot++, item);

                if (itemSlot > 17) {
                    break;
                }
            }
        });
    }
}
