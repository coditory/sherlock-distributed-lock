package com.coditory.sherlock;

import com.coditory.sherlock.connector.AcquireResult;
import com.coditory.sherlock.connector.ReleaseResult;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.coditory.sherlock.Preconditions.expectNonNull;

final class LockResultLogger {
    private final String lockId;
    private final Logger logger;

    LockResultLogger(@NotNull String lockId, @NotNull Class<?> lockType) {
        expectNonNull(lockId, "lockId");
        expectNonNull(lockType, "lockType");
        this.lockId = lockId;
        this.logger = LoggerFactory.getLogger(lockType);
    }

    void logResult(@NotNull AcquireResult result) {
        expectNonNull(result, "result");
        if (result.isAcquired()) {
            logger.debug("Lock acquired: {}", lockId);
        } else {
            logger.debug("Lock not acquired: {}", lockId);
        }
    }

    void logResult(@NotNull ReleaseResult result) {
        expectNonNull(result, "result");
        if (result.isReleased()) {
            logger.debug("Lock released: {}", lockId);
        } else {
            logger.debug("Lock not released: {}", lockId);
        }
    }
}
