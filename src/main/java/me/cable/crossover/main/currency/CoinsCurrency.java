package me.cable.crossover.main.currency;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class CoinsCurrency extends LocalCurrency {

    private static final DecimalFormat FORMAT = new DecimalFormat("0");

    public CoinsCurrency() {
        super("coins");
    }

    @Override
    public @NotNull String name() {
        return "coins";
    }

    @Override
    public @NotNull String format(@NotNull BigDecimal value, boolean showType) {
        StringBuilder sb = new StringBuilder(FORMAT.format(value));

        if (showType) {
            sb.append(" coin").append(value.equals(BigDecimal.ONE) ? "" : "s");
        }

        return sb.toString();
    }
}
