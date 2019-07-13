package com.coditory.sherlock;

import com.coditory.sherlock.common.LockId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class LockResultLogger {
  private final String lockId;
  private final Logger logger;

  LockResultLogger(LockId lockId, Class<?> lockType) {
    this.lockId = lockId.getValue();
    this.logger = LoggerFactory.getLogger(lockType);
  }

  boolean logAcquireResult(boolean acquired) {
    if (acquired) {
      logger.debug("Lock acquired: {}", lockId);
    } else {
      logger.debug("Lock not acquired: {}", lockId);
    }
    return acquired;
  }

  boolean logReleaseResult(boolean released) {
    if (released) {
      logger.debug("Lock released: {}", lockId);
    } else {
      logger.debug("Lock not released: {}", lockId);
    }
    return released;
  }
}
