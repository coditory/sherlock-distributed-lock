# SQL Distributed Lock

SQL connector enables distributed locking on a relational databases.
It was tested on [Postrgres v11]({{ vcs_baseurl }}/sql/src/integration/groovy/com/coditory/sherlock/base/PostgresInitializer.groovy)
and [MySQL v8]({{ vcs_baseurl }}/sql/src/integration/groovy/com/coditory/sherlock/base/MySqlInitializer.groovy).

### Locks Table

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

### Synchronous SQL Sherlock

!!! warning "Synchronous API only"
    SQL connector provides synchronous API only. There is no reliable reactive jdbc driver.

```java
Properties connectionProps = new Properties();
connectionProps.put("user", "mysql");
connectionProps.put("password", "mysql");
Connection dbConnection = return DriverManager
    .getConnection("jdbc:mysql://localhost:${mysql.firstMappedPort}/mysql", connectionProps);
Sherlock sherlock = sqlSherlock()
  .withClock(Clock.systemDefaultZone())
  .withLockDuration(Duration.ofMinutes(5))
  .withUniqueOwnerId()
  .withConnection(dbConnection)
  .withLocksTable("LOCKS")
  .build();
// ...or simply
// Sherlock sherlockWithDefaults = sqlSherlock(dbConnection);
```

!!! info "Learn more"
    See the full sample on [Github]({{ vcs_baseurl }}/sample/src/main/java/com/coditory/sherlock/sample/SqlSyncSample.java),
    read sherlock builder [javadoc](https://www.javadoc.io/page/com.coditory.sherlock/sherlock-sql/latest/com/coditory/sherlock/SqlSherlockBuilder.html).
