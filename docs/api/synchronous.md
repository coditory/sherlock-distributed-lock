# Synchronous Distributed Lock

Add dependencies to `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.coditory.sherlock:sherlock-mongo:{{ version }}")
}
```

Create a lock:
```java
// Get mongo locks collection
String database = "sherlock";
MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017/" + database);
MongoCollection<Document> collection = mongoClient
    .getDatabase("sherlock")
    .getCollection("locks");
// Create sherlock
Sherlock sherlock = MongoSherlock.builder()
    .withLocksCollection(collection)
    .build();
// Create a lock
DistributedLock lock = sherlock.createLock("sample-lock");
```

Acquire a lock:
```java
// Acquire a lock
if (lock.acquire()) {
  try {
    System.out.println("Lock granted!");
  } finally {
    lock.release();
  }
}
```

...or acquire a lock in a more concise way:
```java
lock.acquireAndExecute(() -> {
  System.out.println("Lock granted!");
});
```
