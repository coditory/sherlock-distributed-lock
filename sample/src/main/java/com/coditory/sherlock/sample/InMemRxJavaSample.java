package com.coditory.sherlock.sample;

import com.coditory.sherlock.RxDistributedLock;
import com.coditory.sherlock.RxSherlock;
import io.reactivex.Single;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;

import static com.coditory.sherlock.ReactiveInMemorySherlockBuilder.reactiveInMemorySherlockBuilder;

public class InMemRxJavaSample {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  void sampleInMemSherlock() {
    RxSherlock sherlock = reactiveInMemorySherlockBuilder()
      .withClock(Clock.systemDefaultZone())
      .withUniqueOwnerId()
      .withSharedStorage()
      .buildWithApi(RxSherlock::rxSherlock);
    // ...or simply
    // RxSherlock sherlockWithDefaults = rxSherlock(reactiveInMemorySherlock());
    RxDistributedLock lock = sherlock.createLock("sample-lock");
    lock.acquireAndExecute(Single.fromCallable(() -> {
      logger.info("Lock acquired!");
      return true;
    })).blockingGet();
  }

  public static void main(String[] args) {
    new InMemRxJavaSample().sampleInMemSherlock();
  }
}
