package com.coditory.sherlock.sample.inmem;

import com.coditory.sherlock.ReactorDistributedLock;
import com.coditory.sherlock.ReactorSherlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.time.Clock;

import static com.coditory.sherlock.ReactorInMemorySherlockBuilder.reactiveInMemorySherlockBuilder;

public class InMemReactorSample {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    void sampleInMemLockUsage() {
        ReactorSherlock sherlock = reactiveInMemorySherlockBuilder()
                .withClock(Clock.systemDefaultZone())
                .withUniqueOwnerId()
                .withSharedStorage()
                .buildWithApi(ReactorSherlock::reactorSherlock);
        // ...or simply
        // ReactorSherlock sherlockWithDefaults = reactorSherlock(reactiveInMemorySherlock());
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
