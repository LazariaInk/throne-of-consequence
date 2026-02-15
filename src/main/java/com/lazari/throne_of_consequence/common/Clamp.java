package com.lazari.throne_of_consequence.common;

public final class Clamp {
    private Clamp() {}

    public static int between(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
