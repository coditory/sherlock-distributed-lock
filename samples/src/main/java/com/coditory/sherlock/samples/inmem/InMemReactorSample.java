package com.coditory.sherlock.samples.inmem;

import com.coditory.sherlock.reactor.ReactorDistributedLock;
import com.coditory.sherlock.reactor.ReactorSherlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.time.Clock;

import static com.coditory.sherlock.inmem.reactor.ReactorInMemorySherlockBuilder.reactorInMemorySherlockBuilder;

public class InMemReactorSample {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    void sampleInMemLockUsage() {
        ReactorSherlock sherlock = reactorInMemorySherlockBuilder()
                .withClock(Clock.systemUTC())
                .withUniqueOwnerId()
                .withSharedStorage()
                .build();
        // ...or short equivalent:
        // ReactorSherlock sherlockWithDefaults = reactorInMemorySherlock();
        ReactorDistributedLock lock = sherlock.createLock("sample-lock");
        lock.acquireAndExecute(Mono.fromCallable(() -> {
            logger.info("Lock acquired!");
            return true;
        })).block();
    }

    public static void main(String[] args) {
        new InMemReactorSample().sampleInMemLockUsage();
    }
}
