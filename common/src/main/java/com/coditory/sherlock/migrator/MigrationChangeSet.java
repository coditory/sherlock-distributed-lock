package com.coditory.sherlock.migrator;

import java.util.function.Supplier;

public class MigrationChangeSet<R> {
    private final String id;
    private final Supplier<R> action;

    public MigrationChangeSet(String id, Supplier<R> action) {
        this.id = id;
        this.action = action;
    }

    public String getId() {
        return id;
    }

    public R execute() {
        return action.get();
    }
}
