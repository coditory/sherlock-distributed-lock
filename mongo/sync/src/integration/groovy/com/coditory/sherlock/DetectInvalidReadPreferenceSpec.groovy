package com.coditory.sherlock


import com.mongodb.client.MongoCollection
import org.bson.Document
import spock.lang.Specification

import static com.coditory.sherlock.MongoSherlockBuilder.mongoSherlock

class DetectInvalidReadPreferenceSpec extends Specification {
    def "should throw error when creating sherlock with mongo readPReference != primary"() {
        given:
            MongoCollection<Document> collection = MongoHolder.getClient("?readPreference=nearest")
                    .getDatabase(MongoHolder.databaseName)
                    .getCollection("locks")
        when:
            mongoSherlock()
                    .withLocksCollection(collection)
                    .build()
        then:
            IllegalArgumentException exception = thrown(IllegalArgumentException)
            exception.message == "Expected Mongo connection with readPreference=primary"
    }
}
