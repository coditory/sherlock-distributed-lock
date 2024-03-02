package com.coditory.sherlock

import com.mongodb.kotlin.client.coroutine.MongoClient
import groovy.transform.CompileStatic

@CompileStatic
class KtMongoClientHolder {
    private static MongoClient mongoClient

    synchronized static MongoClient getClient() {
        if (mongoClient != null) return mongoClient
        MongoHolder.startDb()
        String url = MongoHolder.getConnectionString()
        mongoClient = MongoClient.@Factory.create(url)
        return mongoClient
    }
}
