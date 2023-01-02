package com.coditory.sherlock

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testcontainers.containers.MySQLContainer

import javax.sql.DataSource
import java.sql.Connection
import java.sql.DriverManager

@CompileStatic
class MySqlHolder {
    private static final Logger LOGGER = LoggerFactory.getLogger(MySqlHolder)
    private static ResumableMySQLContainer db
    private static started = false
    private static DataSource connectionPool = null

    synchronized static Connection getConnection() {
        startDb()
        Properties properties = new Properties()
        properties.put("user", db.getUsername())
        properties.put("password", db.getPassword())
        return DriverManager.getConnection(db.getJdbcUrl(), properties)
    }

    synchronized static DataSource getConnectionPool() {
        if (connectionPool != null) {
            return connectionPool
        }
        startDb()
        HikariConfig config = new HikariConfig()
        config.setJdbcUrl(db.getJdbcUrl())
        config.setUsername(db.getUsername())
        config.setPassword(db.getPassword())
        config.setConnectionTimeout(10000)
        connectionPool = new HikariDataSource(config)
        return connectionPool
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
