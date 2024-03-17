package com.coditory.sherlock.sql.coroutines

import com.coditory.sherlock.Preconditions.expectNonEmpty
import com.coditory.sherlock.Preconditions.expectNonNull
import com.coditory.sherlock.SherlockDefaults
import com.coditory.sherlock.coroutines.KtSherlock
import com.coditory.sherlock.coroutines.KtSherlockWithConnectorBuilder
import com.coditory.sherlock.sql.BindingMapper
import io.r2dbc.spi.ConnectionFactory
import java.time.Clock

/**
 * Builds [KtSherlock] that uses SQL database for locking mechanism.
 */
class KtSqlSherlockBuilder private constructor() : KtSherlockWithConnectorBuilder<KtSqlSherlockBuilder>() {
    private var tableName = DEFAULT_LOCKS_TABLE_NAME
    private var clock = SherlockDefaults.DEFAULT_CLOCK
    private var connectionFactory: ConnectionFactory? = null
    private var bindingMapper: BindingMapper? = null

    /**
     * @param connectionFactory to the database
     * @return the instance
     */
    fun withConnectionFactory(connectionFactory: ConnectionFactory): KtSqlSherlockBuilder {
        this.connectionFactory = connectionFactory
        return this
    }

    /**
     * @param tableName the name of the table that stores locks
     * @return the instance
     */
    fun withLocksTable(tableName: String): KtSqlSherlockBuilder {
        this.tableName = expectNonEmpty(tableName, "tableName")
        return this
    }

    /**
     * Parameterized statements are vendor specific.
     * That's why you must specify the binding notation with a bindingParameterMapper.
     *
     * @param bindingMapper the connection pool to the database
     * @return the instance
     * @link https://r2dbc.io/spec/1.0.0.RELEASE/spec/html/#statements.parameterized
     */
    fun withBindingMapper(bindingMapper: BindingMapper): KtSqlSherlockBuilder {
        this.bindingMapper = bindingMapper
        return this
    }

    /**
     * @param clock time provider used in locking mechanism. Default: [SherlockDefaults.DEFAULT_CLOCK]
     * @return the instance
     */
    fun withClock(clock: Clock): KtSqlSherlockBuilder {
        this.clock = clock
        return this
    }

    /**
     * @return sherlock instance
     * @throws IllegalArgumentException when some required values are missing
     */
    override fun build(): KtSherlock {
        expectNonNull(connectionFactory, "connectionFactory")
        expectNonNull(bindingMapper, "bindingMapper")
        val connector = KtSqlDistributedLockConnector(connectionFactory!!, tableName, bindingMapper!!, clock)
        return super.build(connector)
    }

    companion object {
        const val DEFAULT_LOCKS_TABLE_NAME = "locks"

        /**
         * @return new instance of the builder
         */
        @JvmStatic
        fun coroutineSqlSherlock(): KtSqlSherlockBuilder {
            return KtSqlSherlockBuilder()
        }

        /**
         * @param connectionFactory to the database
         * @return sql sherlock with default configuration
         */
        @JvmStatic
        fun coroutineSqlSherlock(connectionFactory: ConnectionFactory): KtSherlock {
            return coroutineSqlSherlock()
                .withConnectionFactory(connectionFactory)
                .build()
        }
    }
}
