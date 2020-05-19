package com.coditory.sherlock

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import groovy.transform.CompileStatic
import org.testcontainers.containers.MongoDBContainer

@CompileStatic
class MongoHolder {
    static final String databaseName = "test"
    private static MongoClient mongoClient

    synchronized static MongoClient getClient() {
        if (mongoClient == null) {
            mongoClient = startDb()
        }
        return mongoClient
    }

    private static MongoClient startDb() {
        MongoDBContainer db = new MongoDBContainer("mongo:3.4")
        db.start()
        return MongoClients.create(db.getReplicaSetUrl())
    }
}
