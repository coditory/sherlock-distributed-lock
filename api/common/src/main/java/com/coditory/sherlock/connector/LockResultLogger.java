package com.coditory.sherlock.connector;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.coditory.sherlock.Preconditions.expectNonNull;

public final class LockResultLogger {
    private final String lockId;
    private final Logger logger;

    public LockResultLogger(@NotNull String lockId, @NotNull Class<?> lockType) {
        expectNonNull(lockId, "lockId");
        expectNonNull(lockType, "lockType");
        this.lockId = lockId;
        this.logger = LoggerFactory.getLogger(lockType);
    }

    public void logResult(@NotNull AcquireResult result) {
        expectNonNull(result, "result");
        if (result.isAcquired()) {
            logger.debug("Lock acquired: {}", lockId);
        } else {
            logger.debug("Lock not acquired: {}", lockId);
        }
    }

    public void logResult(@NotNull ReleaseResult result) {
        expectNonNull(result, "result");
        if (result.isReleased()) {
            logger.debug("Lock released: {}", lockId);
        } else {
            logger.debug("Lock not released: {}", lockId);
        }
    }
}
