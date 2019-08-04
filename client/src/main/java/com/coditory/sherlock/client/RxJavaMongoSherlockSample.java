package com.coditory.sherlock.client;

import com.coditory.sherlock.ReactiveMongoSherlock;
import com.coditory.sherlock.connector.AcquireResult;
import com.coditory.sherlock.RxDistributedLock;
import com.coditory.sherlock.RxSherlock;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;
import org.bson.Document;

import static com.coditory.sherlock.RxSherlock.toRxSherlock;

public class RxJavaMongoSherlockSample {
  static RxSherlock createSherlock() {
    String database = "sherlock";
    MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017/" + database);
    MongoCollection<Document> collection = mongoClient
      .getDatabase("sherlock")
      .getCollection("locks");
    return toRxSherlock(ReactiveMongoSherlock.builder()
      .withLocksCollection(collection)
      .build());
  }

  public static void main(String[] args) {
    RxSherlock sherlock = createSherlock();
    RxDistributedLock lock = sherlock.createLock("sample-acquire");
    lock.acquire()
      .filter(AcquireResult::isAcquired)
      .flatMapSingle(result -> {
        System.out.println("Lock granted!");
        return lock.release();
      })
      .blockingGet();
  }
}
