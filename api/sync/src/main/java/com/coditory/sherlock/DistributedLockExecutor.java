package com.coditory.sherlock;

import com.coditory.sherlock.DistributedLock.AcquireAndExecuteResult;
import com.coditory.sherlock.DistributedLock.ReleaseAndExecuteResult;

final class DistributedLockExecutor {
    private DistributedLockExecutor() {
        throw new UnsupportedOperationException("Do not instantiate utility class");
    }

    static AcquireAndExecuteResult executeOnAcquired(boolean acquired, Runnable action, Runnable release) {
        if (acquired) {
            try {
                action.run();
            } finally {
                release.run();
            }
        }
        return new AcquireAndExecuteResult(acquired);
    }

    static ReleaseAndExecuteResult executeOnReleased(boolean released, Runnable action) {
        if (released) {
            action.run();
        }
        return new ReleaseAndExecuteResult(released);
    }
}
