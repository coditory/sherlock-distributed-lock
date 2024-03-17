package com.coditory.sherlock;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class UuidGenerator {
    private UuidGenerator() {
        throw new UnsupportedOperationException("Do not instantiate utility class");
    }

    @NotNull
    public static String uuid() {
        return UUID.randomUUID()
                .toString();
    }
}
