## Sherlock Installation

Different [APIs](api) and [Connectors](connectors) require different dependencies defined in `build.gradle`.

Installing [synchronous Sherlock](api/synchronous.md) with [MongoDB Connector](connectors/mongo.md):
```groovy
dependencies {
    implementation "com.coditory.sherlock:sherlock-mongo-sync:{{ version }}"
}
```

Installing [reactive Sherlock with Reactor API](api/reactor.md) and [MongoDB Connector](connectors/mongo.md):
```groovy
dependencies {
    implementation "com.coditory.sherlock:sherlock-mongo-reactive:{{ version }}"
    implementation "com.coditory.sherlock:sherlock-api-reactor:{{ version }}"
}
```

Installing [reactive Sherlock with RxJava API](api/rxjava.md) and [MongoDB Connector](connectors/mongo.md):
```groovy
dependencies {
    implementation "com.coditory.sherlock:sherlock-mongo-reactive:{{ version }}"
    implementation "com.coditory.sherlock:sherlock-api-rxjava:{{ version }}"
}
```

Configuration looks similar for different connectors and can be generalized into:
```groovy
// synchronous
dependencies {
    implementation "com.coditory.sherlock:sherlock-$CONNECTOR-sync:{{ version }}"
}

// reactor api
dependencies {
    implementation "com.coditory.sherlock:sherlock-$CONNECTOR-reactive:{{ version }}"
    implementation "com.coditory.sherlock:sherlock-api-reactor:{{ version }}"
}

// rxjava api
dependencies {
    implementation "com.coditory.sherlock:sherlock-$CONNECTOR-reactive:{{ version }}"
    implementation "com.coditory.sherlock:sherlock-api-rxjava:{{ version }}"
}
```
