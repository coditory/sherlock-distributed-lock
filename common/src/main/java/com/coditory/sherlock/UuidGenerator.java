package com.coditory.sherlock;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Random unique id generator
 */
public final class UuidGenerator {
    private UuidGenerator() {
        throw new UnsupportedOperationException("Do not instantiate utility class");
    }

    /**
     * @return random unique id
     */
    @NotNull
    public static String uuid() {
        return UUID.randomUUID()
                .toString()
                .replace("-", "");
    }
}
