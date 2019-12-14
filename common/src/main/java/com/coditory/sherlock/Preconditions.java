package com.coditory.sherlock;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Preconditions for sherlock distributed lock. Throws {@link IllegalArgumentException} if
 * precondition is not met.
 */
final class Preconditions {
  private Preconditions() {
    throw new IllegalStateException("Do not instantiate utility class");
  }

  @SafeVarargs
  static <T> T expectAll(T value, String message, BiFunction<T, String, T>... expects) {
    for (BiFunction<T, String, T> expect : expects) {
      expect.apply(value, message);
    }
    return value;
  }

  static <T> T expectNonNull(T value) {
    return expectNonNull(value, "Expected non null value");
  }

  static <T> T expectNonNull(T value, String message) {
    if (value == null) {
      throw new IllegalArgumentException(message);
    }
    return value;
  }

  static void expect(boolean invariant) {
    if (!invariant) {
      throw new IllegalArgumentException();
    }
  }

  static void expect(boolean invariant, String message) {
    if (!invariant) {
      throw new IllegalArgumentException(message);
    }
  }

  static <T> T expectEqual(T value, T expected) {
    return expectEqual(
      value, expected, String.format("Expected %s to be equal %s", value, expected));
  }

  static <T> T expectEqual(T value, T expected, String message) {
    if (value != expected) {
      throw new IllegalArgumentException(message);
    }
    return value;
  }

  static String expectNonEmpty(String value) {
    return expectNonEmpty(value, "Expected non empty string. Got: " + value);
  }

  static String expectNonEmpty(String value, String message) {
    if (value == null || value.trim().isEmpty()) {
      throw new IllegalArgumentException(message);
    }
    return value;
  }

  static <K, E> Map<K, E> expectNonEmpty(Map<K, E> map) {
    return expectNonEmpty(map, "Expected non empty map. Got: " + map);
  }

  static <K, E> Map<K, E> expectNonEmpty(Map<K, E> map, String message) {
    if (map == null || map.isEmpty()) {
      throw new IllegalArgumentException(message);
    }
    return map;
  }

  static <E> List<E> expectNonEmpty(List<E> list) {
    return expectNonEmpty(list, "Expected non empty list. Got: " + list);
  }

  static <E> List<E> expectNonEmpty(List<E> list, String message) {
    if (list == null || list.isEmpty()) {
      throw new IllegalArgumentException(message);
    }
    return list;
  }

  static Duration expectTruncatedToMillis(Duration duration) {
    return expectTruncatedToMillis(
      duration, "Expected duration truncated to millis. Got: " + duration);
  }

  static Duration expectTruncatedToMillis(Duration duration, String message) {
    Duration truncated = duration.truncatedTo(ChronoUnit.MILLIS);
    if (duration != truncated) {
      throw new IllegalArgumentException(message);
    }
    return duration;
  }
}
