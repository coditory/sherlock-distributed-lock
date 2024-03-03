package com.coditory.sherlock.sample.inmem;

import com.coditory.sherlock.RxDistributedLock;
import com.coditory.sherlock.RxSherlock;
import io.reactivex.Single;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;

import static com.coditory.sherlock.RxInMemorySherlockBuilder.rxInMemorySherlockBuilder;

public class InMemRxJavaSample {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    void sampleInMemLockUsage() {
        RxSherlock sherlock = rxInMemorySherlockBuilder()
                .withClock(Clock.systemUTC())
                .withUniqueOwnerId()
                .withSharedStorage()
                .build();
        // ...or short equivalent:
        // RxSherlock sherlockWithDefaults = rxInMemorySherlock(reactiveInMemorySherlock());
        RxDistributedLock lock = sherlock.createLock("sample-lock");
        lock.acquireAndExecute(Single.fromCallable(() -> {
            logger.info("Lock acquired!");
            return true;
        })).blockingGet();
    }

    public static void main(String[] args) {
        new InMemRxJavaSample().sampleInMemLockUsage();
    }
}
