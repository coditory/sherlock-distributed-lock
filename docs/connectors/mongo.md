# Mongo Distributed Lock

MongoDB connector enables distributed locking on [MongoDB](https://www.mongodb.com/).
It was [tested on MongoDB v3.4]({{ vcs_baseurl }}/mongo/sync/src/integration/groovy/com/coditory/sherlock/MongoHolder.groovy).

!!! warning "Read and write from the same DB node"
    Set mongo driver [`readPreference=primary`](https://docs.mongodb.com/manual/core/read-preference/#mongodb-readmode-primary)
    so every lock change is visible to all of your services.

### Locks collection

Sample lock document:

```json
{
  "_id": "lock-id",
  "acquiredBy": "owner-id",
  "acquiredAt": { "$date": 1562502838189 },
  "expiresAt": { "$date": 1562503458189 }
}
```

### Synchronous MongoDB Sherlock

Creating synchronous mongo sherlock:
```java
String database = "sherlock";
MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017/" + database);
MongoCollection<Document> locksCollection = mongoClient
  .getDatabase(database)
  .getCollection("locks");
Sherlock sherlock = mongoSherlock()
  .withClock(Clock.systemDefaultZone())
  .withLockDuration(Duration.ofMinutes(5))
  .withUniqueOwnerId()
  .withLocksCollection(locksCollection)
  .build();
// ...or simply
// Sherlock sherlockWithDefaults = mongoSherlock(locksCollection);
```

!!! info "Learn more"
    See the full sample on [Github]({{ vcs_baseurl }}/sample/src/main/java/com/coditory/sherlock/sample/mongo/MongoSyncSample.java),
    read sherlock builder [javadoc](https://www.javadoc.io/page/com.coditory.sherlock/sherlock-mongo-sync/latest/com/coditory/sherlock/MongoSherlockBuilder.html).

### Reactive MongoDB Sherlock

Creating reactive mongo sherlock:
```java
String database = "sherlock";
MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017/" + database);
MongoCollection<Document> locksCollection = mongoClient
  .getDatabase(database)
  .getCollection("locks");
ReactorSherlock sherlock = reactiveInMemorySherlockBuilder()
  .withClock(Clock.systemDefaultZone())
  .withUniqueOwnerId()
  .withLocksCollection(locksCollection)
  .buildWithApi(ReactorSherlock::reactorSherlock);
// ...or simply
// ReactorSherlock sherlockWithDefaults = reactorSherlock(reactiveInMemorySherlock(locksCollection));
```

!!! info "Learn more"
    See the full sample on [Github]({{ vcs_baseurl }}/sample/src/main/java/com/coditory/sherlock/sample/MongoReactorSample.java),
    read sherlock builder [javadoc](https://www.javadoc.io/page/com.coditory.sherlock/sherlock-mongo-sync/latest/com/coditory/sherlock/ReactiveMongoSherlockBuilder.html).

RxJava can be created in a similar way, see the sample on [Github]({{ vcs_baseurl }}/sample/src/main/java/com/coditory/sherlock/sample/MongoRxJavaSample.java).
