# SQL Distributed Lock

SQL connector enables distributed locking on a relational databases.
It was tested on [PostgreSQL v11]({{ vcs_baseurl }}/sql/src/integration/groovy/com/coditory/sherlock/base/PostgresInitializer.groovy)
and [MySQL v8]({{ vcs_baseurl }}/sql/src/integration/groovy/com/coditory/sherlock/base/MySqlInitializer.groovy).

!!! important "Read and write from the same DB node"
    Make sure that DB connection passed to Sherlock reads and writes to the same DB node
    so every lock change is visible to all of your services.

## Usage

Add dependencies to `build.gradle.kts`:

=== "Sync"
    ```kotlin
    dependencies {
        implementation("com.coditory.sherlock:sherlock-sql:{{ version }}")
        implementation("com.zaxxer:HikariCP:$versions.hikaricp")
        implementation("org.postgresql:postgresql:$versions.postgresql")
    }
    ```
=== "Coroutines"
    ```kotlin
    dependencies {
        implementation("com.coditory.sherlock:sherlock-sql-coroutines:{{ version }}")
        implementation("org.postgresql:postgresql:$versions.postgres")
        implementation("com.zaxxer:HikariCP:$versions.hikaricp")
        implementation("org.postgresql:r2dbc-postgresql:$versions.r2dbc")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$versions.coroutines")
    }
    ```
=== "Reactor"
    ```kotlin
    dependencies {
        implementation("com.coditory.sherlock:sherlock-sql-reactor:{{ version }}")
        implementation("org.postgresql:postgresql:$versions.postgres")
        implementation("com.zaxxer:HikariCP:$versions.hikaricp")
        implementation("org.postgresql:r2dbc-postgresql:$versions.r2dbc")
    }
    ```
=== "RxJava"
    ```kotlin
    dependencies {
        implementation("com.coditory.sherlock:sherlock-sql-rxjava:{{ version }}")
        implementation("org.postgresql:postgresql:$versions.postgres")
        implementation("com.zaxxer:HikariCP:$versions.hikaricp")
        implementation("org.postgresql:r2dbc-postgresql:$versions.r2dbc")
    }
    ```

Create sherlock instance and distributed lock:
=== "Sync"
    ```java
    --8<-- "examples/postgres-sync/src/main/java/com/coditory/sherlock/samples/postgres/sync/PostgresSyncLockSample.java:2"
    ```
=== "Coroutines"
    ```kotlin
    --8<-- "examples/postgres-coroutines/src/main/kotlin/com/coditory/sherlock/samples/postgres/coroutines/PostgresKtLockSample.kt:2"
    ```
=== "Reactor"
    ```java
    --8<-- "examples/postgres-reactor/src/main/java/com/coditory/sherlock/samples/postgres/reactor/PostgresReactorLockSample.java:2"
    ```
=== "RxJava"
    ```java 
    --8<-- "examples/postgres-rxjava/src/main/java/com/coditory/sherlock/samples/postgres/rxjava/PostgresRxLockSample.java:2"
    ```

These examples use [Hikari Connection Pool](https://github.com/brettwooldridge/HikariCP), but any implementation
of `java.sql.DataSource` will suffice.

!!! info "Learn more"
    See full source code example on [Github]({{ vcs_baseurl }}/sample/src/main/java/com/coditory/sherlock/example/).

## Configuration

Configuration is available via sherlock builder:
=== "Sync"
    ```java
    ```
=== "Coroutines"
    ```kotlin
    ```
=== "Reactor"
    ```java
    ```
=== "RxJava"
    ```java
    ```

Parameters:

- `clock` (default: `Clock.systemUTC()`) - used to generate acquisition and expiration timestamps.
- `lockDuration` (default: `Duration.ofMinutes(5)`) - used a default lock expiration time.
  If lock is not released and expiration time passes, the lock automatically becomes released.
- `ownerId` (default: `UniqueOwnerId()`) - used to identify lock owner.
  There are different policies available for generating the ownerId.
- `locksCollection` - MongoDb collection used to store the locks.

## Locks Table

Locks table is automatically created if it does not already exist.
Table is created with a following SQL:

```sql
CREATE TABLE LOCKS (
  -- Lock id
  ID VARCHAR(100) NOT NULL,
  -- Owner id
  ACQUIRED_BY VARCHAR(100) NOT NULL,
  -- Lock acquisition moment
  ACQUIRED_AT TIMESTAMP(3) NOT NULL,
  -- Lock expiration time
  -- Might be null for locks that do not expire
  EXPIRES_AT TIMESTAMP(3),
  PRIMARY KEY (ID)
)
CREATE INDEX LOCKS_IDX ON LOCKS (ID, ACQUIRED_BY, EXPIRES_AT)
```
