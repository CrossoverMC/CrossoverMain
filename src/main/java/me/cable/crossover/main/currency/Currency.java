package me.cable.crossover.main.currency;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.*;

public abstract class Currency {

    private static final Map<String, Currency> registeredCurrencies = new HashMap<>();

    public static void register(@NotNull LocalCurrency localCurrency) {
        if (registeredCurrencies.containsKey(localCurrency.getId())) {
            throw new IllegalStateException("Currency with ID " + localCurrency.getId() + " has already been registered");
        }

        registeredCurrencies.put(localCurrency.getId(), localCurrency);
    }

    public static @NotNull List<String> getCurrencies() {
        return new ArrayList<>(registeredCurrencies.keySet());
    }

    public static @Nullable Currency getCurrencyIfExists(@NotNull String id) {
        return registeredCurrencies.get(id);
    }

    public static @NotNull Currency getCurrency(@NotNull String id) {
        Currency currency = registeredCurrencies.get(id);

        if (currency == null) {
            throw new IllegalArgumentException("Invalid currency: " + id);
        }

        return currency;
    }

    public static @NotNull Currency getCoins() {
        return getCurrency("coins");
    }

    public static @NotNull Currency getMoney() {
        return getCurrency("money");
    }

    public abstract @NotNull String name();

    public abstract @NotNull String format(@NotNull BigDecimal value, boolean showType);

    public abstract @NotNull BigDecimal get(@NotNull UUID playerUuid);

    public abstract void set(@NotNull UUID playerUuid, @NotNull BigDecimal amount);

    public void deposit(@NotNull UUID playerUuid, @NotNull BigDecimal amount) {
        set(playerUuid, get(playerUuid).add(amount));
    }

    public void withdraw(@NotNull UUID playerUuid, @NotNull BigDecimal amount) {
        set(playerUuid, get(playerUuid).subtract(amount));
    }
}
