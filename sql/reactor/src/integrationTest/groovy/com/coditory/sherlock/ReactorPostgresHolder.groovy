package com.coditory.sherlock

import groovy.transform.CompileStatic
import io.r2dbc.pool.ConnectionPool
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
import java.sql.Statement
import java.time.Duration

@CompileStatic
class ReactorPostgresHolder {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReactorPostgresHolder)
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
        return new ConnectionPool(configuration)
    }

    synchronized static Connection getBlockingConnection() {
        startDb()
        Properties properties = new Properties()
        properties.put("user", db.getUsername())
        properties.put("password", db.getPassword())
        return DriverManager.getConnection(db.getJdbcUrl(), properties)
    }

    synchronized static void startDb() {
        if (db != null && started) return
        if (db == null) {
            db = new ResumablePostgreSQLContainer("postgres:12", Ports.nextAvailablePort())
            db.start()
        } else {
            db.resume()
        }
        started = true
        waitToConnect()
        LOGGER.info(">>> STARTED: Postgres " + db.getJdbcUrl())
    }

    private static void waitToConnect() {
        int retries = 50
        boolean connected = false
        Throwable lastError = null
        while (retries > 0 && !connected) {
            Thread.sleep(1000)
            try (
                    Connection connection = getBlockingConnection()
                    Statement statement = connection.createStatement()
            ) {
                String result = statement.execute("SELECT 1").toString()
                connected = result == "true"
            } catch (Throwable e) {
                lastError = e
                LOGGER.info("Connection failure, retrying. Left attempts: " + retries)
            }
            retries--
        }
        if (retries == 0) {
            throw new IllegalStateException("Could not connect to Postgres", lastError)
        }
    }

    synchronized static void stopDb() {
        if (!started) return
        db.pause()
        started = false
        LOGGER.info("<<< STOPPED: Postgres " + db.getJdbcUrl())
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
