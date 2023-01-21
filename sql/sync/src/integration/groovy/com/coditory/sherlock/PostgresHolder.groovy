package com.coditory.sherlock

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testcontainers.containers.PostgreSQLContainer

import javax.sql.DataSource
import java.sql.Connection
import java.sql.DriverManager

@CompileStatic
class PostgresHolder {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostgresHolder)
    private static ResumablePostgreSQLContainer db
    private static started = false
    private static DataSource dataSource = null

    synchronized static Connection getConnection() {
        startDb()
        Properties properties = new Properties()
        properties.put("user", db.getUsername())
        properties.put("password", db.getPassword())
        return DriverManager.getConnection(db.getJdbcUrl(), properties)
    }

    synchronized static DataSource getDataSource() {
        if (dataSource != null) {
            return dataSource
        }
        startDb()
        HikariConfig config = new HikariConfig()
        config.setJdbcUrl(db.getJdbcUrl())
        config.setUsername(db.getUsername())
        config.setPassword(db.getPassword())
        config.setConnectionTimeout(10000)
        dataSource = new HikariDataSource(config)
        return dataSource
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
