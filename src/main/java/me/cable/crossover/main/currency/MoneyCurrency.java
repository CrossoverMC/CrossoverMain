package me.cable.crossover.main.currency;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class MoneyCurrency extends LocalCurrency {

    private static final DecimalFormat FORMAT = new DecimalFormat("0.00");

    public MoneyCurrency() {
        super("money");
    }

    @Override
    public @NotNull String name() {
        return "money";
    }

    @Override
    public @NotNull String format(@NotNull BigDecimal value, boolean showType) {
        return (showType ? "$" : "") + FORMAT.format(value);
    }
}
