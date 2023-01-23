package com.coditory.sherlock;

import org.jetbrains.annotations.NotNull;

public class Timer {
    static Timer start() {
        return new Timer();
    }

    private final long started = System.currentTimeMillis();

    long elapsedMs() {
        return System.currentTimeMillis() - started;
    }

    @NotNull
    String elapsed() {
        return elapsedMs() + "ms";
    }
}
