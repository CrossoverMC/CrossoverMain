package me.cable.crossover.main.util;

public final class NumberUtils {

    public static int random(int from, int to) {
        return from + (int) (Math.random() * (Math.abs(from - to) + 1));
    }
}
