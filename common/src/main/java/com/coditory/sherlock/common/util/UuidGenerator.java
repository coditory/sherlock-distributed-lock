package com.coditory.sherlock.common.util;

import java.util.UUID;

public final class UuidGenerator {
  private UuidGenerator() {
    throw new IllegalStateException("Do not instantiate utility class");
  }

  public static String uuid() {
    return UUID.randomUUID()
        .toString()
        .replace("-", "");
  }
}
