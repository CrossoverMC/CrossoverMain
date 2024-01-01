package me.cable.crossover.main.util;

public final class NumberUtils {

    public static double minMax(double value, double min, double max) {
        return Math.min(Math.max(value, min), max);
    }

    public static int random(int from, int to) {
        return from + (int) (Math.random() * (Math.abs(from - to) + 1));
    }

    public static double random(double from, double to) {
        return from + Math.random() * Math.abs(from - to);
    }
}
