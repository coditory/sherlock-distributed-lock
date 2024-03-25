package com.coditory.sherlock.coroutines.migrator

import com.coditory.sherlock.Timer
import com.coditory.sherlock.coroutines.DistributedLock
import com.coditory.sherlock.coroutines.Sherlock
import com.coditory.sherlock.migrator.MigrationResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class SherlockMigrator internal constructor(
    private val migrationLock: DistributedLock,
    private val migrationChangeSets: List<MigrationChangeSet>,
) {
    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    suspend fun migrate(): MigrationResult {
        val acquireResult = migrationLock.runLocked { runMigrations() }
            .onNotAcquired { logger.debug("Migration skipped: {}. Migration lock was refused.", migrationLock.id) }
        return MigrationResult(acquireResult.isAcquired)
    }

    private suspend fun runMigrations() {
        val timer = Timer.start()
        logger.info("Migration started: {}", migrationLock.id)
        migrationChangeSets.forEach { it.execute() }
        logger.info("Migration finished successfully: {} [{}]", migrationLock.id, timer.elapsed())
    }

    internal class MigrationChangeSet(private val id: String, private val lock: DistributedLock, private val action: suspend () -> Any?) {
        private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

        suspend fun execute() {
            val timer = Timer.start()
            if (lock.acquire()) {
                logger.debug("Executing migration change set: {}", id)
                try {
                    action()
                    logger.info("Migration change set applied: {} [{}]", id, timer.elapsed())
                } catch (exception: Throwable) {
                    logger.warn(
                        "Migration change set failure: {} [{}]. Stopping migration process. Fix problem and rerun the migration.",
                        id,
                        timer.elapsed(),
                        exception,
                    )
                    lock.release()
                    throw exception
                }
            } else {
                logger.info("Migration change set skipped: {}", id)
            }
        }
    }

    companion object {
        fun builder(sherlock: Sherlock): SherlockMigratorBuilder {
            return SherlockMigratorBuilder(sherlock)
        }
    }
}
