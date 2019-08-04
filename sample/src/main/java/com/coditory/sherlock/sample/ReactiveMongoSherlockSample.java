package com.coditory.sherlock.sample;

import com.coditory.sherlock.ReactiveMongoSherlock;
import com.coditory.sherlock.connector.AcquireResult;
import com.coditory.sherlock.ReactorDistributedLock;
import com.coditory.sherlock.ReactorSherlock;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;
import org.bson.Document;

import static com.coditory.sherlock.ReactorSherlock.toReactorSherlock;

public final class ReactiveMongoSherlockSample {
  static ReactorSherlock createSherlock() {
    String database = "sherlock";
    MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017/" + database);
    MongoCollection<Document> collection = mongoClient
      .getDatabase(database)
      .getCollection("locks");
    return toReactorSherlock(ReactiveMongoSherlock.builder()
      .withLocksCollection(collection)
      .build());
  }

  public static void main(String[] args) {
    ReactorSherlock sherlock = createSherlock();
    ReactorDistributedLock lock = sherlock.createLock("sample-acquire");
    lock.acquire()
      .filter(AcquireResult::isAcquired)
      .flatMap(result -> {
        System.out.println("Lock granted!");
        return lock.release();
      })
      .block();
  }
}
