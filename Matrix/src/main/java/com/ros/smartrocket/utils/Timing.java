package com.ros.smartrocket.utils;

public final class Timing {
    public static final double DOUBLE = 1e6d;
    private long startTime;

    public Timing() {
        startTime = System.nanoTime();
    }

    public String measure() {
        long currentTime = System.nanoTime();
        long diff = currentTime - startTime;
        startTime = currentTime;

        return String.format("Passed %.1fms", diff / DOUBLE);
    }
}
