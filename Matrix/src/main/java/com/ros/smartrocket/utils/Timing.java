package com.ros.smartrocket.utils;

public final class Timing {
    public static final double DOUBLE = 1e6d;
    private final String tag;
    private long startTime;

    public Timing() {
        this("");
    }

    public Timing(String tag) {
        this.tag = tag;
        startTime = System.nanoTime();
    }

    public String measure() {
        long currentTime = System.nanoTime();
        long diff = currentTime - startTime;
        startTime = currentTime;

        return String.format("%s %.1fms", tag, diff / DOUBLE);
    }
}
