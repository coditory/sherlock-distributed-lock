package com.coditory.sherlock.samples.inmem.reactor;

import com.coditory.sherlock.inmem.reactor.InMemorySherlock;
import com.coditory.sherlock.reactor.DistributedLock;
import com.coditory.sherlock.reactor.Sherlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

public class InMemReactorLockSample {
    private static final Logger logger = LoggerFactory.getLogger(InMemReactorLockSample.class);

    public static void main(String[] args) {
        Sherlock sherlock = InMemorySherlock.create();
        DistributedLock lock = sherlock.createLock("sample-lock");
        lock.acquireAndExecute(Mono.fromCallable(() -> {
            logger.info("Lock acquired!");
            return true;
        })).block();
    }
}
