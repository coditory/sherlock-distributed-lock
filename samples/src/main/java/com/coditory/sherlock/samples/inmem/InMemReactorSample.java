package com.coditory.sherlock.samples.inmem;

import com.coditory.sherlock.inmem.reactor.InMemorySherlock;
import com.coditory.sherlock.reactor.DistributedLock;
import com.coditory.sherlock.reactor.Sherlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.time.Clock;

public class InMemReactorSample {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    void sampleInMemLockUsage() {
        Sherlock sherlock = InMemorySherlock.builder()
                .withClock(Clock.systemUTC())
                .withUniqueOwnerId()
                .withSharedStorage()
                .build();
        // ...or short equivalent:
        // ReactorSherlock sherlockWithDefaults = reactorInMemorySherlock();
        DistributedLock lock = sherlock.createLock("sample-lock");
        lock.acquireAndExecute(Mono.fromCallable(() -> {
            logger.info("Lock acquired!");
            return true;
        })).block();
    }

    public static void main(String[] args) {
        new InMemReactorSample().sampleInMemLockUsage();
    }
}
