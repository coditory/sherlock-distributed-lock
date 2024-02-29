package com.coditory.sherlock.base

import org.junit.jupiter.api.Assertions.fail
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
suspend fun <T : Throwable> assertThrows(
    action: suspend () -> Unit,
    type: KClass<T>,
): T {
    return try {
        action()
        fail("Expected thrown exception")
    } catch (e: Throwable) {
        if (!type.isInstance(e)) {
            throw e
        }
        e as T
    }
}
