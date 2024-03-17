package com.coditory.sherlock.samples.inmem;

import com.coditory.sherlock.inmem.rxjava.InMemorySherlock;
import com.coditory.sherlock.rxjava.DistributedLock;
import com.coditory.sherlock.rxjava.Sherlock;
import io.reactivex.Single;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;

public class InMemRxJavaSample {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    void sampleInMemLockUsage() {
        Sherlock sherlock = InMemorySherlock.builder()
                .withClock(Clock.systemUTC())
                .withUniqueOwnerId()
                .withSharedStorage()
                .build();
        // ...or short equivalent:
        // RxSherlock sherlockWithDefaults = rxInMemorySherlock(reactiveInMemorySherlock());
        DistributedLock lock = sherlock.createLock("sample-lock");
        lock.acquireAndExecute(Single.fromCallable(() -> {
            logger.info("Lock acquired!");
            return true;
        })).blockingGet();
    }

    public static void main(String[] args) {
        new InMemRxJavaSample().sampleInMemLockUsage();
    }
}
