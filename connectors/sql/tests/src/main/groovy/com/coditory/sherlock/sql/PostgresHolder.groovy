package com.coditory.sherlock.sql

import com.coditory.sherlock.Ports
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testcontainers.containers.PostgreSQLContainer

import javax.sql.DataSource
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement

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
        if (dataSource != null) return dataSource
        startDb()
        dataSource = getDataSource({})
        return dataSource
    }

    synchronized static DataSource getDataSource(DataSourceConfigurer configurer) {
        startDb()
        HikariConfig config = new HikariConfig()
        config.setJdbcUrl(db.getJdbcUrl())
        config.setUsername(db.getUsername())
        config.setPassword(db.getPassword())
        config.setConnectionTimeout(10000)
        config.setMaximumPoolSize(10)
        configurer.configure(config)
        return new HikariDataSource(config)
    }

    synchronized static void startDb() {
        if (db != null && started) return
        if (db == null) {
            db = new ResumablePostgreSQLContainer("postgres:17", Ports.getNextAvailable())
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
                Connection connection = getConnection()
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

    synchronized static String getJdbcUrl() {
        if (db == null) return null
        return db.getJdbcUrl()
    }

    synchronized static String getUsername() {
        if (db == null) return null
        return db.getUsername()
    }

    synchronized static String getPassword() {
        if (db == null) return null
        return db.getPassword()
    }

    synchronized static String getDatabaseName() {
        if (db == null) return null
        return db.getDatabaseName()
    }

    static class ResumablePostgreSQLContainer extends PostgreSQLContainer {
        private final int port

        ResumablePostgreSQLContainer(String dockerImageName, int port) {
            super(dockerImageName)
            this.port = port
            this.addFixedExposedPort(port, 5432)
        }

        void pause() {
            dockerClient.stopContainerCmd(getContainerId()).exec()
        }

        void resume() {
            dockerClient.startContainerCmd(getContainerId()).exec()
        }

        void close() {
            super.close()
        }
    }
}
