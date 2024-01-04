package me.cable.crossover.main.currency;

import me.cable.crossover.main.handler.PlayerData;
import me.cable.crossover.main.util.ConfigHelper;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * A currency which is stored in this plugin's data folder.
 */
public abstract class LocalCurrency extends Currency {

    private final String id;

    public LocalCurrency(@NotNull String id) {
        this.id = id;
    }

    public final void register() {
        Currency.register(this);
    }

    @Override
    public @NotNull BigDecimal get(@NotNull UUID playerUuid) {
        return new ConfigHelper(PlayerData.get(playerUuid)).bd("currencies." + id);
    }

    @Override
    public void set(@NotNull UUID playerUuid, @NotNull BigDecimal amount) {
        PlayerData.get(playerUuid).set("currencies." + id, amount.stripTrailingZeros().toPlainString());
    }

    public final @NotNull String getId() {
        return id;
    }
}
