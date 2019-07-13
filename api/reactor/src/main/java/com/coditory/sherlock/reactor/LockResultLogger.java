package com.coditory.sherlock.reactor;

import com.coditory.sherlock.reactive.connector.LockResult;
import com.coditory.sherlock.reactive.connector.ReleaseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class LockResultLogger {
  private final String lockId;
  private final Logger logger;

  LockResultLogger(String lockId, Class<?> lockType) {
    this.lockId = lockId;
    this.logger = LoggerFactory.getLogger(lockType);
  }

  void logResult(LockResult result) {
    if (result.isLocked()) {
      logger.debug("Lock acquired: {}", lockId);
    } else {
      logger.debug("Lock not acquired: {}", lockId);
    }
  }

  void logResult(ReleaseResult result) {
    if (result.isUnlocked()) {
      logger.debug("Lock released: {}", lockId);
    } else {
      logger.debug("Lock not released: {}", lockId);
    }
  }
}
