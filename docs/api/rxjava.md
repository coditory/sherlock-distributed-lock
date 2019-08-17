# RxJava API

Add dependencies to `build.gradle`:

```groovy
dependencies {
  compile "com.coditory.sherlock:sherlock-mongo-reactive:0.4.0"
  compile "com.coditory.sherlock:sherlock-api-rxjava:0.4.0"
}
```

Create reactive lock with RxJava API:
```java
// Get mongo locks collection
String database = "sherlock";
MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017/" + database);
MongoCollection<Document> collection = mongoClient
    .getDatabase(database)
    .getCollection("locks");
// Create sherlock
RxSherlock sherlock = ReactiveMongoSherlock.builder()
    .withLocksCollection(collection)
    .buildWithApi(RxSherlock::rxSherlock);
// Create a lock with reactor api
RxDistributedLock lock = sherlock.createLock("sample-lock");
```

Acquire a lock:
```java
// Acquire a lock
lock.acquire()
  .filter(LockResult::isLocked)
  .flatMap(result -> {
    System.out.println("Lock granted!");
    return lock.release();
  })
  .blockingGet();
```

...or shorter
```java
lock.acquireAndExecute(Single.fromCallable(() -> {
  logger.info("Lock acquired!");
  return true;
})).blockingGet();
```
