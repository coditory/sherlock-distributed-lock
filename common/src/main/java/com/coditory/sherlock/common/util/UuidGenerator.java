package com.coditory.sherlock.common.util;

import java.util.UUID;

/**
 * Random unique id generator
 */
public final class UuidGenerator {
  private UuidGenerator() {
    throw new IllegalStateException("Do not instantiate utility class");
  }

  /**
   * @return random unique id
   */
  public static String uuid() {
    return UUID.randomUUID()
        .toString()
        .replace("-", "");
  }
}
