package com.coditory.sherlock

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testcontainers.containers.MongoDBContainer

@CompileStatic
final class MongoHolder {
    public static final String databaseName = "test"
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoHolder.class)
    private static ResumableMongoDBContainer db
    private static boolean started = false
    private static MongoClient mongoClient

    synchronized static MongoClient getClient() {
        if (mongoClient != null) {
            return mongoClient
        }
        startDb()
        String url = db.getConnectionString()
        mongoClient = MongoClients.create(url)
        return mongoClient
    }

    synchronized static String getConnectionString() {
        if (db == null) return null
        return db.getConnectionString()
    }

    synchronized static void startDb() {
        if (db != null && started) return
        if (db == null) {
            db = new ResumableMongoDBContainer("mongo:3.6", Ports.nextAvailablePort())
            db.start()
        } else {
            db.resume()
        }
        started = true
        waitForMongoToStart()
        LOGGER.info(">>> STARTED: MongoDb " + db.getConnectionString())
    }

    private static void waitForMongoToStart() {
        int retries = 50
        while (retries > 0 && !getClient().getClusterDescription().hasWritableServer()) {
            try {
                Thread.sleep(1000)
            } catch (InterruptedException e) {
                throw new IllegalStateException("Sleep interrupted", e)
            }
            retries--
        }
        if (retries == 0) {
            throw new IllegalStateException("No writable server available")
        }
    }

    synchronized static void stopDb() {
        if (!started) return
        String connectionString = db.getConnectionString()
        db.pause()
        started = false
        LOGGER.info("<<< STOPPED: MongoDb " + connectionString)
    }

    static class ResumableMongoDBContainer extends MongoDBContainer {
        private final int port

        ResumableMongoDBContainer(String dockerImageName, int port) {
            super(dockerImageName)
            this.port = port
            this.addFixedExposedPort(port, 27017)
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
