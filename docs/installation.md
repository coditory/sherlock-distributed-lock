## Sherlock Installation

Different [APIs](../api) and [Connectors](../connectors) require different dependencies defined in `build.gradle.kts`.

```kotlin
// Synchronous API
dependencies {
    implementation("com.coditory.sherlock:sherlock-$CONNECTOR:{{ version }}")
}

// Reactor API
dependencies {
    implementation("com.coditory.sherlock:sherlock-$CONNECTOR-reactor:{{ version }}")
}

// RxJava API
dependencies {
    implementation("com.coditory.sherlock:sherlock-$CONNECTOR-rxjava:{{ version }}")
}

// Kotlin Coroutine API
dependencies {
    implementation("com.coditory.sherlock:sherlock-$CONNECTOR-coroutine:{{ version }}")
}
```

So far there are 3 types of connectors: `mongo`, `sql`, `in-mem`.