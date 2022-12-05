# SQL Distributed Lock

SQL connector enables distributed locking on a relational databases.
It was tested on [Postrgres v11]({{ vcs_baseurl }}/sql/src/integration/groovy/com/coditory/sherlock/base/PostgresInitializer.groovy)
and [MySQL v8]({{ vcs_baseurl }}/sql/src/integration/groovy/com/coditory/sherlock/base/MySqlInitializer.groovy).

## Synchronous SQL Sherlock

Add dependency to `build.gradle`:

```groovy
dependencies {
    implementation "org.postgresql:postgresql:$versions.postgresql"
    // ...or MySQL
    // implementation "mysql:mysql-connector-java:8.0.27"
    // ...or any other SQL driver
    implementation "com.zaxxer:HikariCP:$versions.hikaricp"
    // ...or any other SQL Connection Pool
    implementation "com.coditory.sherlock:sherlock-sql:{{ version }}"
}
```

!!! warning "Synchronous API only"
    SQL connector provides synchronous API only.

```java
HikariConfig config=new HikariConfig();
config.setJdbcUrl("jdbc:postgresql://localhost:5432/test");
config.setUsername("postgres");
config.setPassword("postgres");
DataSource connectionPool=new HikariDataSource(config);

Sherlock sherlock=sqlSherlock()
  .withClock(Clock.systemDefaultZone())
  .withLockDuration(Duration.ofMinutes(5))
  .withUniqueOwnerId()
  .withConnectionPool(connectionPool)
  .withLocksTable("LOCKS")
  .build();
// ...or simply
// Sherlock sherlockWithDefaults = sqlSherlock(connectionPool);
```

This example uses [Hikari Connection Pool](https://github.com/brettwooldridge/HikariCP), but any implementation
of `java.sql.DataSource` will suffice.

!!! info "Learn more"
    See the full sample on [Github]({{ vcs_baseurl }}/sample/src/main/java/com/coditory/sherlock/sample/mysql/MySqlSyncSample.java),
    read sherlock builder [javadoc](https://www.javadoc.io/page/com.coditory.sherlock/sherlock-sql/latest/com/coditory/sherlock/SqlSherlockBuilder.html).

## Locks Table

Locks table is automatically created if it did not already exist.
Table is created with a following SQL:

```sql
CREATE TABLE LOCKS (
  ID VARCHAR(100) NOT NULL,
  ACQUIRED_BY VARCHAR(100) NOT NULL,
  ACQUIRED_AT TIMESTAMP(3) NOT NULL,
  EXPIRES_AT TIMESTAMP(3),
  PRIMARY KEY (ID)
)
```

Table name may be changed during sherlock creation.