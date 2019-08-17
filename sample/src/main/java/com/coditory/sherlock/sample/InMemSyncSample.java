package com.coditory.sherlock.sample;

import com.coditory.sherlock.DistributedLock;
import com.coditory.sherlock.Sherlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;

import static com.coditory.sherlock.InMemorySherlockBuilder.inMemorySherlockBuilder;

public class InMemSyncSample {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  void sampleInMemSherlock() {
    Sherlock sherlock = inMemorySherlockBuilder()
      .withClock(Clock.systemDefaultZone())
      .withUniqueOwnerId()
      .withSharedStorage()
      .build();
    // ...or simply
    // Sherlock sherlockWithDefaults = inMemorySherlock();
    DistributedLock lock = sherlock.createLock("sample-lock");
    lock.acquireAndExecute(() -> logger.info("Lock acquired!"));
  }

  public static void main(String[] args) {
    new InMemSyncSample().sampleInMemSherlock();
  }
}
