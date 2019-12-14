package com.coditory.sherlock;

import com.coditory.sherlock.connector.AcquireResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.coditory.sherlock.Preconditions.expectNonEmpty;
import static com.coditory.sherlock.Preconditions.expectNonNull;

public class ReactorSherlockMigrator {
  private static final String DEFAULT_MIGRATOR_LOCK_ID = "migrator";
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final List<MigrationChangeSet> migrationChangeSets = new ArrayList<>();
  private final String migrationId;
  private final ReactorSherlock sherlock;
  private final ReactorDistributedLock migrationLock;
  private final Set<String> migrationLockIds = new HashSet<>();

  public static ReactorSherlockMigrator reactorSherlockMigrator(ReactorSherlock sherlock) {
    return reactorSherlockMigrator(DEFAULT_MIGRATOR_LOCK_ID, sherlock);
  }

  public static ReactorSherlockMigrator reactorSherlockMigrator(
    String migrationId, ReactorSherlock sherlock) {
    return new ReactorSherlockMigrator(migrationId, sherlock);
  }

  /**
   * @param sherlock sherlock used to manage migration locks
   */
  public ReactorSherlockMigrator(ReactorSherlock sherlock) {
    this(DEFAULT_MIGRATOR_LOCK_ID, sherlock);
  }

  /**
   * @param migrationId id used as lock id for the whole migration process
   * @param sherlock    sherlock used to manage migration locks
   */
  public ReactorSherlockMigrator(String migrationId, ReactorSherlock sherlock) {
    this.migrationId = migrationId;
    this.sherlock = sherlock;
    this.migrationLock = sherlock.createLock()
      .withLockId(migrationId)
      .withPermanentLockDuration()
      .withStaticUniqueOwnerId()
      .build();
    this.migrationLockIds.add(migrationId);
  }

  /**
   * Adds change set to migration process.
   *
   * @param changeSetId unique change set id used. This is is used as a lock id in migration
   *                    process.
   * @param changeSet   change set action that should be run if change set was not already applied
   * @return the migrator
   */
  public ReactorSherlockMigrator addChangeSet(String changeSetId, Mono<?> changeSet) {
    expectNonEmpty(changeSetId, "Expected non empty changeSetId");
    ensureUniqueChangeSetId(changeSetId);
    migrationLockIds.add(changeSetId);
    ReactorDistributedLock changeSetLock = createChangeSetLock(changeSetId);
    MigrationChangeSet migrationChangeSet = new MigrationChangeSet(
      changeSetId, changeSetLock, changeSet);
    migrationChangeSets.add(migrationChangeSet);
    return this;
  }

  public ReactorSherlockMigrator addAnnotatedChangeSets(Object object) {
    expectNonNull(object, "Expected non null object containing change sets");
    ChangeSetMethodExtractor.extractChangeSets(object, Mono.class)
      .forEach(changeSet -> addChangeSet(changeSet.getId(), changeSet.execute()));
    return this;
  }

  private ReactorDistributedLock createChangeSetLock(String changeSetId) {
    return sherlock.createLock()
      .withLockId(changeSetId)
      .withPermanentLockDuration()
      .withStaticUniqueOwnerId()
      .build();
  }

  /**
   * Runs the migration process.
   *
   * @return migration result
   */
  public Flux<ChangeSetResult> migrate() {
    if (migrationChangeSets.isEmpty()) {
      return Flux.empty();
    }
    String lastMigrationId = migrationChangeSets.get(migrationChangeSets.size() - 1).id;
    return migrationLock.acquire()
      .doOnNext(this::logMigrationLock)
      .filter(AcquireResult::isAcquired)
      .thenMany(runMigrations())
      .flatMap(result -> lastMigrationId.equals(result.id)
        ? migrationLock.release().then(Mono.just(result))
        : Mono.just(result)
      );
  }

  private void logMigrationLock(AcquireResult result) {
    if (!result.isAcquired()) {
      logger.debug(
        "Migration skipped: {}. Migration lock refused.",
        migrationLock.getId());
    }
  }

  private Flux<ChangeSetResult> runMigrations() {
    Timer timer = Timer.start();
    logger.info("Migration started: {}", migrationId);
    return Flux.fromIterable(migrationChangeSets)
      .flatMap(MigrationChangeSet::execute, 1)
      .doOnComplete(() -> logger
        .info("Migration finished successfully: {} [{}]", migrationId, timer.elapsed()));
  }

  private void ensureUniqueChangeSetId(String changeSetId) {
    if (migrationLockIds.contains(changeSetId)) {
      throw new IllegalArgumentException(
        "Expected unique change set ids. Duplicated id: " + changeSetId);
    }
  }

  private static class MigrationChangeSet {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String id;
    private final ReactorDistributedLock lock;
    private final Mono<?> action;

    MigrationChangeSet(String id, ReactorDistributedLock lock, Mono<?> action) {
      this.id = id;
      this.lock = lock;
      this.action = action;
    }

    Mono<ChangeSetResult> execute() {
      return lock.acquire()
        .doOnNext(this::logChangeSetLockResult)
        .filter(AcquireResult::isAcquired)
        .map(__ -> Timer.start())
        .flatMap(action::thenReturn)
        .doOnNext(
          timer -> logger.info("Migration change set applied: {} [{}]", id, timer.elapsed()))
        .onErrorResume(exception -> {
          logger.warn(
            "Migration change set failure: {}. Stopping migration process. Fix problem and rerun the migration.",
            id, exception);
          return lock.release().then(Mono.error(exception));
        })
        .map(__ -> new ChangeSetResult(id, true))
        .defaultIfEmpty(new ChangeSetResult(id, false));
    }

    private void logChangeSetLockResult(AcquireResult result) {
      if (result.isAcquired()) {
        logger.debug("Executing migration change set: {}", id);
      } else {
        logger.info("Migration change set skipped: {}. It was already applied", id);
      }
    }
  }

  static public class ChangeSetResult {
    private final String id;
    private final boolean migrated;

    public static ChangeSetResult migratedChangeSet(String id) {
      return new ChangeSetResult(id, true);
    }

    public static ChangeSetResult skippedChangeSet(String id) {
      return new ChangeSetResult(id, false);
    }

    ChangeSetResult(String id, boolean migrated) {
      this.id = id;
      this.migrated = migrated;
    }

    public String getId() {
      return id;
    }

    public boolean isMigrated() {
      return migrated;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      ChangeSetResult that = (ChangeSetResult) o;
      return migrated == that.migrated &&
        Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
      return Objects.hash(id, migrated);
    }

    @Override
    public String toString() {
      return "ChangeSetResult{" +
        "id='" + id + '\'' +
        ", migrated=" + migrated +
        '}';
    }
  }
}
