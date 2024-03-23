# Migrator

Distributed locks may be used for multiple purposes one of them is a one way database migration process:

```java
// prepare the migration
SherlockMigrator migrator = new SherlockMigrator("db-migration", sherlock)
  .addChangeSet("add db index", () -> /* ... */)
  .addChangeSet("remove stale collection", () -> /* ... */)

// run the migration
migrator.migrate();
```

Migration rules:

- migrations must not be run in parallel (neither by one nor by multiple machines)
- migration change sets are applied in order
- migration change set must be run only once per all migrations
- migration process stops when first change set fails
