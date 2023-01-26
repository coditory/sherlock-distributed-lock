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
        if (db != null && started) {
            return
        }
        started = true
        if (db == null) {
            db = new ResumableMySQLContainer("mysql:8", Ports.nextAvailablePort())
            db.start()
        } else {
            db.resume()
        }
        LOGGER.info(">>> STARTED: MySql " + db.getJdbcUrl())
    }

    synchronized static void stopDb() {
        if (db != null && started) {
            String jdbcUrl = db.getJdbcUrl()
            db.pause()
            started = false
            LOGGER.info("<<< STOPPED: MySql " + jdbcUrl)
        }
    }

    static class ResumableMySQLContainer extends MySQLContainer {
        private final int port

        ResumableMySQLContainer(String dockerImageName, int port) {
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
            this.addFixedExposedPort(port, 3306)
            super.start()
        }

        void close() {
            super.close()
        }
    }
}
