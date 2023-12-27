package me.cable.crossover.main.currency;

import me.cable.crossover.main.util.ConfigHelper;
import me.cable.crossover.main.CrossoverMain;
import me.cable.crossover.main.handler.PlayerData;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * A currency which is stored in this plugin's data folder.
 */
public class LocalCurrency extends Currency {

    private final PlayerData playerData;
    private final String id;

    public LocalCurrency(@NotNull String id) {
        playerData = CrossoverMain.getInstance().getPlayerData();
        this.id = id;
    }

    public final void register() {
        Currency.register(this);
    }

    @Override
    public @NotNull BigDecimal get(@NotNull UUID playerUuid) {
        return new ConfigHelper(playerData.get(playerUuid)).bd("currencies." + id);
    }

    @Override
    public void set(@NotNull UUID playerUuid, @NotNull BigDecimal amount) {
        playerData.get(playerUuid).set("currencies." + id, amount.toPlainString());
    }

    public final @NotNull String getId() {
        return id;
    }
}
