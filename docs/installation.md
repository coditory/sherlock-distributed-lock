## Sherlock Installation

Different [APIs](/api) and [Connectors](/connectors) require different dependencies defined in `build.gradle`.

Installing [synchronous Sherlock](/api#synchronous-api) with [MongoDB Connector](/connectors#mongodb-connector) requires single dependency:
```groovy
dependencies {
  compile "com.coditory.sherlock:sherlock-mongo-sync:0.4.0"
}
```

Installing [reactive Sherlock with Reactor API](/api#reactor-api) and [MongoDB Connector](/connectors#mongodb-connector) requires two dependencies:
```groovy
dependencies {
  compile "com.coditory.sherlock:sherlock-mongo-reactive:0.4.0"
  compile "com.coditory.sherlock:sherlock-api-reactor:0.4.0"
}
```

Installing [reactive Sherlock with Reactor API](/api#rxjava-api) and MongoDB Connector requires two dependencies:
```groovy
dependencies {
  compile "com.coditory.sherlock:sherlock-mongo-reactive:0.4.0"
  compile "com.coditory.sherlock:sherlock-api-rxjava:0.4.0"
}
```

Configuration looks similar for different connectors and can be generalized into:
```groovy
// synchronous
dependencies {
  compile "com.coditory.sherlock:sherlock-$CONNECTOR-sync:0.4.0"
}

// reactor api
dependencies {
  compile "com.coditory.sherlock:sherlock-$CONNECTOR-reactive:0.4.0"
  compile "com.coditory.sherlock:sherlock-api-reactor:0.4.0"
}

// rxjava api
dependencies {
  compile "com.coditory.sherlock:sherlock-$CONNECTOR-reactive:0.4.0"
  compile "com.coditory.sherlock:sherlock-api-rxjava:0.4.0"
}
```
