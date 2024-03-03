# Distributed Lock with Reactor API

Add dependencies to `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.coditory.sherlock:sherlock-mongo-coroutine:{{ version }}")
}
```

Create a lock:
```java
// Get mongo locks collection
String database = "sherlock";
MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017/" + database);
MongoCollection<Document> collection = mongoClient
    .getDatabase(database)
    .getCollection("locks");
// Create sherlock
ReactorSherlock sherlock = ReactiveMongoSherlock.builder()
    .withLocksCollection(collection)
    .buildWithApi(ReactorSherlock::reactorSherlock);
// Create a lock with reactor api
ReactorDistributedLock lock = sherlock.createLock("sample-lock");
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
  .block();
```

...or shorter
```java
lock.acquireAndExecute(() -> Mono.just("Lock granted!"))
  .block();
```
