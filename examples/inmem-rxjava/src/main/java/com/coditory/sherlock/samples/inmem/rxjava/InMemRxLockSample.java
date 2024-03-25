package com.coditory.sherlock.samples.inmem.rxjava;

import com.coditory.sherlock.inmem.rxjava.InMemorySherlock;
import com.coditory.sherlock.rxjava.DistributedLock;
import com.coditory.sherlock.rxjava.Sherlock;
import io.reactivex.Single;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InMemRxLockSample {
    private static final Logger logger = LoggerFactory.getLogger(InMemRxLockSample.class);

    public static void main(String[] args) {
        Sherlock sherlock = InMemorySherlock.create();
        DistributedLock lock = sherlock.createLock("sample-lock");
        lock.runLocked(Single.fromCallable(() -> {
            logger.info("Lock acquired!");
            return true;
        })).blockingGet();
    }
}
