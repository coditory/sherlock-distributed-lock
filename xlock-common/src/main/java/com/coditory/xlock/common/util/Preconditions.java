package com.coditory.xlock.common.util;

public class Preconditions {
  public static <T> T expectNonNull(T value) {
    return expectNonNull(value, "Expected non null value");
  }

  public static <T> T expectNonNull(T value, String message) {
    if (value == null) {
      throw new IllegalArgumentException(message);
    }
    return value;
  }

  public static String expectNonEmpty(String value) {
    return expectNonEmpty(value, "Expected non empty string. Got: " + value);
  }

  public static String expectNonEmpty(String value, String message) {
    if (value == null || value.trim().isEmpty()) {
      throw new IllegalArgumentException(message);
    }
    return value;
  }
}
