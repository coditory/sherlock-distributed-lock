package com.coditory.sherlock

import groovy.transform.CompileStatic
import io.r2dbc.pool.ConnectionPoolConfiguration
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions
import io.r2dbc.spi.ValidationDepth
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testcontainers.containers.PostgreSQLContainer

import java.sql.Connection
import java.sql.DriverManager
import java.time.Duration

@CompileStatic
class RxPostgresHolder {
    private static final Logger LOGGER = LoggerFactory.getLogger(RxPostgresHolder)
    private static ResumablePostgreSQLContainer db
    private static started = false
    private static ConnectionFactory connectionFactory = null

    synchronized static ConnectionFactory getConnectionFactory() {
        startDb()
        if (connectionFactory == null) {
            connectionFactory = pooledConnectionFactory()
        }
        return connectionFactory
    }

    private static ConnectionFactory pooledConnectionFactory() {
        ConnectionFactoryOptions options = ConnectionFactoryOptions
                .parse(db.getJdbcUrl().replace("jdbc:", "r2dbc:"))
                .mutate()
                .option(ConnectionFactoryOptions.USER, db.getUsername())
                .option(ConnectionFactoryOptions.PASSWORD, db.getPassword())
                .option(ConnectionFactoryOptions.DATABASE, db.getDatabaseName())
                .build()
        ConnectionFactory connectionFactory = ConnectionFactories.get(options)
        ConnectionPoolConfiguration configuration = ConnectionPoolConfiguration.builder(connectionFactory)
                .maxIdleTime(Duration.ofSeconds(1))
                .maxAcquireTime(Duration.ofSeconds(10))
                .initialSize(10)
                .minIdle(3)
                .maxSize(10)
                .acquireRetry(5)
                .maxValidationTime(Duration.ofSeconds(1))
                .validationQuery("SELECT 1")
                .validationDepth(ValidationDepth.REMOTE)
                .build()
        return new io.r2dbc.pool.ConnectionPool(configuration)
    }

    synchronized static Connection getBlockingConnection() {
        startDb()
        Properties properties = new Properties()
        properties.put("user", db.getUsername())
        properties.put("password", db.getPassword())
        return DriverManager.getConnection(db.getJdbcUrl(), properties)
    }

    synchronized static void startDb() {
        if (db == null) {
            db = new ResumablePostgreSQLContainer("postgres:11", Ports.nextAvailablePort())
            db.start()
            started = true
        } else if (!started) {
            db.resume()
            started = true
        }
        LOGGER.info(">>> STARTED: Postgres " + db.getJdbcUrl())
    }

    synchronized static void stopDb() {
        if (db != null && started) {
            String connectionString = db.getJdbcUrl()
            db.pause()
            started = false
            LOGGER.info("<<< STOPPED: Postgres " + connectionString)
        }
    }

    static class ResumablePostgreSQLContainer extends PostgreSQLContainer {
        private final int port

        ResumablePostgreSQLContainer(String dockerImageName, int port) {
            super(dockerImageName)
            this.port = port
        }

        void pause() {
            dockerClient.stopContainerCmd(getContainerId()).exec()
        }

        void resume() {
            dockerClient.startContainerCmd(getContainerId()).exec()
        }

        void start() {
            this.addFixedExposedPort(port, 5432)
            super.start()
        }

        void close() {
            super.close()
        }
    }
}
