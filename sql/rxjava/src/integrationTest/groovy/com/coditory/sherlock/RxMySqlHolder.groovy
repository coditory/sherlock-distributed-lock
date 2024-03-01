package com.coditory.sherlock

import groovy.transform.CompileStatic
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testcontainers.containers.MySQLContainer

import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement

@CompileStatic
class RxMySqlHolder {
    private static final Logger LOGGER = LoggerFactory.getLogger(RxMySqlHolder)
    private static ResumableMySQLContainer db
    private static started = false
    private static ConnectionFactory connectionFactory = null

    synchronized static ConnectionFactory getConnectionFactory() {
        startDb()
        if (connectionFactory != null) {
            return connectionFactory
        }
        ConnectionFactoryOptions options = ConnectionFactoryOptions
                .parse(db.getJdbcUrl().replace("jdbc:", "r2dbc:"))
                .mutate()
                .option(ConnectionFactoryOptions.USER, db.getUsername())
                .option(ConnectionFactoryOptions.PASSWORD, db.getPassword())
                .option(ConnectionFactoryOptions.DATABASE, db.getDatabaseName())
                .build()
        connectionFactory = ConnectionFactories.get(options)
        return connectionFactory
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
            db = new ResumableMySQLContainer("mysql:8", Ports.nextAvailablePort())
            db.start()
        } else {
            db.resume()
        }
        started = true
        waitToConnect()
        LOGGER.info(">>> STARTED: MySql " + db.getJdbcUrl())
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
            throw new IllegalStateException("Could not connect to MySql", lastError)
        }
    }

    synchronized static void stopDb() {
        if (!started) return
        db.pause()
        started = false
        LOGGER.info("<<< STOPPED: MySql " + db.getJdbcUrl())
    }

    static class ResumableMySQLContainer extends MySQLContainer {
        private final int port

        ResumableMySQLContainer(String dockerImageName, int port) {
            super(dockerImageName)
            this.port = port
            this.withUrlParam("serverTimezone", "UTC")
        }

        void pause() {
            dockerClient.stopContainerCmd(getContainerId()).exec()
        }

        void resume() {
            dockerClient.startContainerCmd(getContainerId()).exec()
        }

        void start() {
            this.addFixedExposedPort(port, 3306)
            super.start()
        }

        void close() {
            super.close()
        }
    }
}
