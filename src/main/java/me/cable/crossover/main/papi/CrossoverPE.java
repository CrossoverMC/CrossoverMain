package me.cable.crossover.main.papi;

import me.cable.crossover.main.currency.Currency;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class CrossoverPE extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "crossover";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Cable";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onRequest(@Nullable OfflinePlayer offlinePlayer, @NotNull String params) {
        if (offlinePlayer == null) return null;
        UUID playerUuid = offlinePlayer.getUniqueId();

        String[] args = params.split("_");

        if (args[0].equals("currency") && args.length >= 2) {
            String currencyId = args[1];
            Currency currency = Currency.getCurrencyIfExists(currencyId);

            if (currency != null) {
                return currency.get(playerUuid).toPlainString();
            }
        }

        return null;
    }
}
