package com.coditory.sherlock.sample;

import com.coditory.sherlock.ReactorDistributedLock;
import com.coditory.sherlock.ReactorSherlock;

import static com.coditory.sherlock.ReactiveInMemorySherlockBuilder.reactiveInMemorySherlock;
import static com.coditory.sherlock.ReactorSherlock.reactorSherlock;

public class InMemSample {
  static ReactorSherlock createReactorSherlock() {
    return reactorSherlock(reactiveInMemorySherlock());
  }

  static void sampleReactorInMemSherlock() {
    ReactorSherlock sherlock = createReactorSherlock();
    ReactorDistributedLock lock = sherlock.createLock("lock");
    lock.acquire()
      .doOnNext(result -> System.out.println("Acquire result: " + result))
      .flatMap(result -> lock.release())
      .doOnNext(result -> System.out.println("Release result: " + result))
      .block();
  }

  public static void main(String[] args) {
    sampleReactorInMemSherlock();
  }
}
