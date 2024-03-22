package com.coditory.sherlock.samples.inmem.reactor;

import com.coditory.sherlock.inmem.reactor.InMemorySherlock;
import com.coditory.sherlock.reactor.DistributedLock;
import com.coditory.sherlock.reactor.Sherlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.time.Clock;

public class InMemReactorLockSample {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Sherlock sherlock = InMemorySherlock.builder()
            .withClock(Clock.systemUTC())
            .withUniqueOwnerId()
            .withSharedStorage()
            .build();

    void sample() {
        DistributedLock lock = sherlock.createLock("sample-lock");
        lock.acquireAndExecute(Mono.fromCallable(() -> {
            logger.info("Lock acquired!");
            return true;
        })).block();
    }

    public static void main(String[] args) {
        new InMemReactorLockSample().sample();
    }
}
