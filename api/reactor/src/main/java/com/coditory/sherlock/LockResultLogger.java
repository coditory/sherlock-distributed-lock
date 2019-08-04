package com.coditory.sherlock;

import com.coditory.sherlock.connector.AcquireResult;
import com.coditory.sherlock.connector.ReleaseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class LockResultLogger {
  private final String lockId;
  private final Logger logger;

  LockResultLogger(String lockId, Class<?> lockType) {
    this.lockId = lockId;
    this.logger = LoggerFactory.getLogger(lockType);
  }

  void logResult(AcquireResult result) {
    if (result.isAcquired()) {
      logger.debug("Lock acquired: {}", lockId);
    } else {
      logger.debug("Lock not acquired: {}", lockId);
    }
  }

  void logResult(ReleaseResult result) {
    if (result.isReleased()) {
      logger.debug("Lock released: {}", lockId);
    } else {
      logger.debug("Lock not released: {}", lockId);
    }
  }
}
