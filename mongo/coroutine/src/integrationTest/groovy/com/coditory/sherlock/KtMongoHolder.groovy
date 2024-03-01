package com.coditory.sherlock

import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testcontainers.containers.MongoDBContainer

@CompileStatic
class KtMongoHolder {
    static final String databaseName = "test"
    private static final Logger LOGGER = LoggerFactory.getLogger(KtMongoHolder)
    private static ResumableMongoDBContainer db
    private static started = false
    private static MongoClient mongoClient

    synchronized static MongoClient getClient() {
        if (mongoClient != null) {
            return mongoClient
        }
        startDb()
        String url = String.format(
            "mongodb://%s:%d/%s",
            db.getHost(),
            db.getPort(),
            databaseName
        )
        mongoClient = MongoClients.create(url)
        return mongoClient
    }

    synchronized static void startDb() {
        if (db != null && started) {
            return
        }
        started = true
        if (db == null) {
            db = new ResumableMongoDBContainer("mongo:3.6", Ports.nextAvailablePort())
            db.start()
        } else {
            db.resume()
        }
        waitForMongoToStart()
        LOGGER.info(">>> STARTED: MongoDb " + db.getConnectionString())
    }

    private static void waitForMongoToStart() {
        int retries = 50
        while (retries > 0 && !getClient().getClusterDescription().hasWritableServer()) {
            Thread.sleep(1000)
            retries--
        }
        if (retries == 0) {
            throw new IllegalStateException("No writable server available")
        }
    }

    synchronized static void stopDb() {
        if (db != null && started) {
            String connectionString = db.getConnectionString()
            db.pause()
            started = false
            LOGGER.info("<<< STOPPED: MongoDb " + connectionString)
        }
    }

    static class ResumableMongoDBContainer extends MongoDBContainer {
        private final int port

        ResumableMongoDBContainer(String dockerImageName, int port) {
            super(dockerImageName)
            this.port = port
        }

        int getPort() {
            return port
        }

        void pause() {
            dockerClient.stopContainerCmd(getContainerId()).exec()
        }

        void resume() {
            this.addFixedExposedPort(port, 27017)
            dockerClient.startContainerCmd(getContainerId()).exec()
        }

        void start() {
            this.addFixedExposedPort(port, 27017)
            super.start()
        }

        void close() {
            super.close()
        }
    }
}
