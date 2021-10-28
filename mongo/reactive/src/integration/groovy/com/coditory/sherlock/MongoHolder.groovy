package com.coditory.sherlock

import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import groovy.transform.CompileStatic
import org.testcontainers.containers.MongoDBContainer

@CompileStatic
class MongoHolder {
    static final String databaseName = "test"
    private static MongoDBContainer db
    private static Map<String, MongoClient> mongoClients = new HashMap<>()

    synchronized static MongoClient getClient() {
        return getClient("")
    }

    synchronized static MongoClient getClient(String connectionQueryString) {
        if (db == null) {
            startDb()
        }
        String url = String.format(
                "mongodb://%s:%d/%s%s",
                db.getContainerIpAddress(),
                db.getMappedPort(27017),
                databaseName,
                normalizeConnectionQueryString(connectionQueryString)
        )
        return mongoClients.computeIfAbsent(url, { MongoClients.create(it) })
    }

    private static String normalizeConnectionQueryString(String connectionQueryString) {
        String trimmed = connectionQueryString.trim()
        if (trimmed.isEmpty()) {
            return ""
        }
        String prefix = connectionQueryString.startsWith("?") ? "" : "?"
        return prefix + trimmed
    }

    private static synchronized void startDb() {
        db = new MongoDBContainer("mongo:3.4")
        db.start()
    }
}
