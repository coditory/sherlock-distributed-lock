package com.coditory.sherlock.samples.inmem.sync;

import com.coditory.sherlock.DistributedLock;
import com.coditory.sherlock.Sherlock;
import com.coditory.sherlock.inmem.InMemorySherlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InMemSyncLockSample {
    private static final Logger logger = LoggerFactory.getLogger(InMemSyncLockSample.class);

    public static void main(String[] args) {
        Sherlock sherlock = InMemorySherlock.create();
        DistributedLock lock = sherlock.createLock("sample-lock");
        lock.acquireAndExecute(() -> logger.info("Lock acquired!"));
    }
}
