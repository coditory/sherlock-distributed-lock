package com.coditory.sherlock;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Preconditions for sherlock distributed lock. Throws {@link IllegalArgumentException} if
 * precondition is not met.
 */
public final class Preconditions {
    private Preconditions() {
        throw new UnsupportedOperationException("Do not instantiate utility class");
    }

    public static void expect(boolean check, String message, Object... args) {
        if (!check) {
            throw new IllegalArgumentException(String.format(message, args));
        }
    }

    public static <T> T expectEqual(T value, T expected) {
        return expectEqual(
            value, expected, String.format("Expected %s to be equal %s", value, expected));
    }

    public static <T> T expectEqual(T value, T expected, String message) {
        if (value != expected) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    public static <K, E> Map<K, E> expectNonEmpty(Map<K, E> map) {
        return expectNonEmpty(map, "Expected non-null and non-empty map. Got: " + map);
    }

    public static <K, E> Map<K, E> expectNonEmpty(Map<K, E> map, String message) {
        if (map == null || map.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return map;
    }

    public static <T> T expectNonNull(T value, String name) {
        if (value == null) {
            String message = message("Expected non-null value", name);
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    public static <T> List<T> expectNonEmpty(List<T> values, String name) {
        if (values == null || values.isEmpty()) {
            String message = message("Expected non-null and non-empty list", name, values);
            throw new IllegalArgumentException(message);
        }
        return values;
    }

    public static String expectNonEmpty(String value, String name) {
        if (value == null || value.isEmpty()) {
            String message = message("Expected non-null and non-empty value", name, value);
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    public static Duration expectTruncatedToMillis(Duration value, String name) {
        Duration truncated = value.truncatedTo(ChronoUnit.MILLIS);
        if (value != truncated) {
            String message = message(
                "Expected duration truncated to millis",
                !Objects.equals(name, "duration") ? name : null,
                value
            );
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    private static String message(String expectation, String fieldName, Object value) {
        String field = fieldName != null ? (": " + fieldName) : "";
        String stringValue = value instanceof String
            ? ("\"" + value + "\"")
            : Objects.toString(value);
        return expectation + field + ". Got: " + stringValue;
    }

    private static String message(String expectation, String fieldName) {
        String field = fieldName != null ? (": " + fieldName) : "";
        return expectation + field;
    }
}
