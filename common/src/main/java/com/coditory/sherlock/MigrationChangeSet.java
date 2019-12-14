package com.coditory.sherlock;

import java.util.function.Supplier;

class MigrationChangeSet<R> {
  private final String id;
  private final Supplier<R> action;

  MigrationChangeSet(String id, Supplier<R> action) {
    this.id = id;
    this.action = action;
  }

  String getId() {
    return id;
  }

  R execute() {
    return action.get();
  }
}
