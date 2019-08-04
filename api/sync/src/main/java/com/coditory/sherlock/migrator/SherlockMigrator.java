package com.coditory.sherlock.migrator;

import com.coditory.sherlock.DistributedLock;
import com.coditory.sherlock.Sherlock;
import com.coditory.sherlock.util.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.coditory.sherlock.util.Preconditions.expectNonEmpty;

public final class SherlockMigrator {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final List<MigrationChangeSet> migrationChangeSets = new ArrayList<>();
  private final String migrationId;
  private final Sherlock sherlock;
  private final DistributedLock migrationLock;

  public SherlockMigrator(String migrationId, Sherlock sherlock) {
    this.migrationId = migrationId;
    this.sherlock = sherlock;
    this.migrationLock = sherlock.createLock()
        .withLockId(migrationId)
        .withPermanentLockDuration()
        .withStaticUniqueOwnerId()
        .build();
  }

  public SherlockMigrator addChangeSet(String changeSetId, Runnable changeSet) {
    expectNonEmpty(changeSetId, "Expected non empty changeSetId");
    DistributedLock changeSetLock = createChangeSetLock(changeSetId);
    migrationChangeSets.add(new MigrationChangeSet(changeSetId, changeSetLock, changeSet));
    return this;
  }

  private DistributedLock createChangeSetLock(String migrationId) {
    return sherlock.createLock()
        .withLockId(migrationId)
        .withPermanentLockDuration()
        .withStaticUniqueOwnerId()
        .build();
  }

  public void migrate() {
    migrationLock.acquireAndExecute(this::runMigrations);
  }

  private void runMigrations() {
    logger.info("Starting migration: {}", migrationId);
    migrationChangeSets.forEach(MigrationChangeSet::execute);
    logger.info("Migration finished successfully: {}", migrationId);
  }

  private static class MigrationChangeSet {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String id;
    private final DistributedLock lock;
    private final Runnable action;

    MigrationChangeSet(String id, DistributedLock lock, Runnable action) {
      this.id = id;
      this.lock = lock;
      this.action = action;
    }

    void execute() {
      if (lock.acquire()) {
        logger.debug("Executing migration change set: {}", id);
        try {
          action.run();
          logger.info("Migration change set applied: {}", id);
        } catch (Throwable exception) {
          logger.warn(
              "Migration change set failure: {}. Stopping migration process. Fix problem and rerun the migration.",
              id, exception);
          lock.release();
          throw exception;
        }
      } else {
        logger.info("Migration change set skipped: {}. It was already applied", id);
      }
    }
  }
}
