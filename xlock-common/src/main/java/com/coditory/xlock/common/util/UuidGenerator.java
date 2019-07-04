package com.coditory.xlock.common.util;

import java.util.UUID;

public class UuidGenerator {
  public static String uuid() {
    return UUID.randomUUID()
        .toString()
        .replace("-", "");
  }
}
