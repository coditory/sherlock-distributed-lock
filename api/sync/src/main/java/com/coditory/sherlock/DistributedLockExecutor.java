package com.coditory.sherlock;

final class DistributedLockExecutor {
  private DistributedLockExecutor() {
    throw new IllegalStateException("Do not instantiate utility class");
  }

  static boolean executeOnAcquired(boolean acquired, Runnable action, Runnable release) {
    if (!acquired) {
      return false;
    }
    try {
      action.run();
    } finally {
      release.run();
    }
    return true;
  }

  static boolean executeOnReleased(boolean released, Runnable action) {
    if (released) {
      action.run();
    }
    return released;
  }
}
