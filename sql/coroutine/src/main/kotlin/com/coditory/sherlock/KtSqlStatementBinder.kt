package com.coditory.sherlock

import com.coditory.sherlock.SqlLockNamedQueriesTemplate.ParameterNames.EXPIRES_AT
import com.coditory.sherlock.SqlLockNamedQueriesTemplate.ParameterNames.LOCK_ID
import com.coditory.sherlock.SqlLockNamedQueriesTemplate.ParameterNames.NOW
import com.coditory.sherlock.SqlLockNamedQueriesTemplate.ParameterNames.OWNER_ID
import io.r2dbc.spi.Statement
import kotlinx.coroutines.reactive.awaitFirst
import java.time.Instant
import kotlin.reflect.KClass

internal class KtSqlStatementBinder(
    private val statement: Statement,
    private val bindingMapper: BindingMapper,
) {
    private var index: Int = 0

    fun bindLockId(value: String?): KtSqlStatementBinder {
        return bind(LOCK_ID, value, String::class)
    }

    fun bindOwnerId(value: String?): KtSqlStatementBinder {
        return bind(OWNER_ID, value, String::class)
    }

    fun bindNow(value: Instant?): KtSqlStatementBinder {
        return bind(NOW, value, Instant::class)
    }

    fun bindExpiresAt(value: Instant?): KtSqlStatementBinder {
        return bind(EXPIRES_AT, value, Instant::class)
    }

    private fun bind(
        name: String,
        value: Any?,
        type: KClass<*>,
    ): KtSqlStatementBinder {
        val key = bindingMapper.mapBinding(index, name).bindingKey
        index++
        if (key is Int) {
            if (value != null) {
                statement.bind(key, value)
            } else {
                statement.bindNull(key, type.java)
            }
        } else {
            val stringKey = key.toString()
            if (value != null) {
                statement.bind(stringKey, value)
            } else {
                statement.bindNull(stringKey, type.java)
            }
        }
        return this
    }

    suspend fun executeAndGetUpdated(): Long {
        val result = statement.execute().awaitFirst()
        return result.rowsUpdated.awaitFirst()
    }
}
