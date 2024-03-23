package com.coditory.sherlock.samples.inmem.rxjava;

import com.coditory.sherlock.inmem.rxjava.InMemorySherlock;
import com.coditory.sherlock.rxjava.DistributedLock;
import com.coditory.sherlock.rxjava.Sherlock;
import io.reactivex.Single;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;

public class InMemRxLockSample {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Sherlock sherlock = InMemorySherlock.builder()
            .withClock(Clock.systemUTC())
            .withUniqueOwnerId()
            .withSharedStorage()
            .build();

    void sample() {
        DistributedLock lock = sherlock.createLock("sample-lock");
        lock.acquireAndExecute(Single.fromCallable(() -> {
            logger.info("Lock acquired!");
            return true;
        })).blockingGet();
    }

    public static void main(String[] args) {
        new InMemRxLockSample().sample();
    }
}
