# In Memory Connector

In Memory connector was created for local development and testing purposes.

### Synchronous in-memory sherlock

```java
Sherlock sherlock = inMemorySherlockBuilder()
  .withClock(Clock.systemDefaultZone())
  .withUniqueOwnerId()
  .withSharedStorage()
  .build();
// ...or simply
// Sherlock sherlockWithDefaults = inMemorySherlock();
```

!!! info "Learn more"
    See the full sample on [Github]({{ vcs_baseurl }}/sample/src/main/java/com/coditory/sherlock/sample/InMemSyncSample.java),
    read sherlock builder [javadoc](https://www.javadoc.io/page/com.coditory.sherlock/sherlock-sql/latest/com/coditory/sherlock/InMemorySherlockBuilder.html).

### Reactive in-memory sherlock

```java
ReactorSherlock sherlock = reactiveInMemorySherlockBuilder()
  .withClock(Clock.systemDefaultZone())
  .withUniqueOwnerId()
  .withSharedStorage()
  .buildWithApi(ReactorSherlock::reactorSherlock);
// ...or simply
// ReactorSherlock sherlockWithDefaults = reactorSherlock(reactiveInMemorySherlock());
```

!!! info "Learn more"
    See the full sample on [Github]({{ vcs_baseurl }}/sample/src/main/java/com/coditory/sherlock/sample/InMemReactorSample.java),
    read sherlock builder [javadoc](https://www.javadoc.io/page/com.coditory.sherlock/sherlock-sql/latest/com/coditory/sherlock/ReactiveInMemorySherlockBuilder.html).

RxJava version can be created in a similar way, see the sample on [Github]({{ vcs_baseurl }}/sample/src/main/java/com/coditory/sherlock/sample/InMemRxJavaSample.java).
