package com.coditory.sherlock;

import org.jetbrains.annotations.NotNull;

public class Timer {
    public static Timer start() {
        return new Timer();
    }

    private final long started = System.currentTimeMillis();

    public long elapsedMs() {
        return System.currentTimeMillis() - started;
    }

    @NotNull
    public String elapsed() {
        return elapsedMs() + "ms";
    }
}
