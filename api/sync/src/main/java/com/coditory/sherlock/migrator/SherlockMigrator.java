package com.coditory.sherlock.migrator;

import com.coditory.sherlock.DistributedLock;
import com.coditory.sherlock.Sherlock;
import com.coditory.sherlock.common.LockId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

public class SherlockMigrator {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final Map<LockId, Runnable> migrations = new LinkedHashMap<>();
  private final DistributedLock migrationLock;
  private final Sherlock sherlock;

  public SherlockMigrator(DistributedLock migrationLock, Sherlock sherlock) {
    this.migrationLock = migrationLock;
    this.sherlock = sherlock;
  }

  public SherlockMigrator addMigration(String id, Runnable migration) {
    migrations.put(LockId.of(id), migration);
    return this;
  }

  public void migrate() {
    migrationLock.acquireAndExecute(this::runMigrations);
  }

  private void runMigrations() {
    logger.info("Acquired migration lock. Starting migration.");
    migrations.forEach(this::runMigration);
    logger.info("Finished migration.");
  }

  private void runMigration(LockId lockId, Runnable runnable) {
    DistributedLock lock = sherlock.createLock(lockId.getValue());
    if (lock.acquireForever()) {
      try {
        runnable.run();
        logger.info("Change set success: {}", lockId.getValue());
      } catch (Throwable exception) {
        logger.info("Change set failure: {}. Stopping migration process.", lockId.getValue(), exception);
        lock.release();
        throw exception;
      }
    } else {
      logger.info("Change set skipped: {}. It was already applied", lockId.getValue());
    }
  }
}
