package com.coditory.sherlock.migrator;

import java.lang.reflect.Method;

public record MigrationChangeSetMethod<R>(String id, Object object, Method method, Class<R> returnType) {
}
