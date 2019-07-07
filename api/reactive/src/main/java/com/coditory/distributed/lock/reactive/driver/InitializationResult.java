package com.coditory.distributed.lock.reactive.driver;

import java.util.Objects;

public class InitializationResult {
  private static final InitializationResult SUCCESS = new InitializationResult(true);
  private static final InitializationResult FAILURE = new InitializationResult(false);

  public static InitializationResult of(boolean value) {
    return value ? SUCCESS : FAILURE;
  }

  private boolean initialized;

  private InitializationResult(boolean initialized) {
    this.initialized = initialized;
  }

  public boolean isInitialized() {
    return initialized;
  }

  @Override
  public String toString() {
    return "InitializationResult{" +
        "initialized=" + initialized +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InitializationResult that = (InitializationResult) o;
    return initialized == that.initialized;
  }

  @Override
  public int hashCode() {
    return Objects.hash(initialized);
  }
}
