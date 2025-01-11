package com.coditory.sherlock.migrator;

import java.util.function.Supplier;

public record MigrationChangeSet<R>(String id, Supplier<R> action) {
    static <R> MigrationChangeSet<R> from(MigrationChangeSetMethod<R> changeSetMethod) {
        return new MigrationChangeSet<>(
            changeSetMethod.id(),
            ChangeSetMethodExtractor.invokeMethod(changeSetMethod.method(), changeSetMethod.object(), changeSetMethod.returnType())
        );
    }

    public R execute() {
        return action.get();
    }
}
