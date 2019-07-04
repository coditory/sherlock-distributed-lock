package com.coditory.xlock.mongo;

import com.coditory.xlock.common.driver.XLockDriver;
import com.mongodb.reactivestreams.client.MongoClient;

import java.time.Duration;

import static com.coditory.xlock.common.util.XLockPreconditions.expectNonNull;

public class XlockMongoDriver implements XLockDriver {
  private final MongoClient mongoClient;

  public XlockMongoDriver(MongoClient mongoClient) {
    this.mongoClient = expectNonNull(mongoClient, "Expected non null mongoClient");
  }

  @Override
  public void lockWithDuration(Duration duration) {
    mongoClient.
  }

  @Override
  public void lockInfinitely() {

  }

  @Override
  public void unlock() {

  }
}
