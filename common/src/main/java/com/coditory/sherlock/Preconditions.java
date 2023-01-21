package com.coditory.sherlock;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

/**
 * Preconditions for sherlock distributed lock. Throws {@link IllegalArgumentException} if
 * precondition is not met.
 */
final class Preconditions {
    private Preconditions() {
        throw new UnsupportedOperationException("Do not instantiate utility class");
    }

    static void expect(boolean check, String message, Object... args) {
        if (!check) {
            throw new IllegalArgumentException(String.format(message, args));
        }
    }

    static <T> T expectNonNull(T value, String name) {
        if (value == null) {
            String message = message("Expected non-null value", name);
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    static <T> List<T> expectNonEmpty(List<T> values, String name) {
        if (values == null || values.isEmpty()) {
            String message = message("Expected non-null and non-empty list", name, values);
            throw new IllegalArgumentException(message);
        }
        return values;
    }

    static String expectNonEmpty(String value, String name) {
        if (value == null || value.isEmpty()) {
            String message = message("Expected non-null and non-empty value", name, value);
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    static Duration expectTruncatedToMillis(Duration value, String name) {
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
