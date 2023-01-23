package com.coditory.sherlock

import io.r2dbc.spi.Connection
import kotlinx.coroutines.reactive.awaitFirstOrNull

suspend inline fun <R> Connection.use(block: (Connection) -> R): R {
    var exception: Throwable? = null
    try {
        return block(this)
    } catch (e: Throwable) {
        exception = e
        throw e
    } finally {
        when (exception) {
            null -> close().awaitFirstOrNull()
            else -> try {
                close().awaitFirstOrNull()
            } catch (closeException: Throwable) {
                // cause.addSuppressed(closeException) // ignored here
            }
        }
    }
}