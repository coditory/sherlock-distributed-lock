package com.coditory.sherlock;

class Timer {
  static Timer start() {
    return new Timer();
  }

  private final long started = System.currentTimeMillis();

  long elapsedMs() {
    return System.currentTimeMillis() - started;
  }

  String elapsed() {
    return elapsedMs() + "ms";
  }
}
