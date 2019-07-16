package com.coditory.sherlock.client;

import com.coditory.sherlock.reactive.ReactiveMongoSherlock;
import com.coditory.sherlock.reactive.connector.AcquireResult;
import com.coditory.sherlock.rxjava.RxJavaDistributedLock;
import com.coditory.sherlock.rxjava.RxJavaSherlock;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;
import org.bson.Document;

public class RxJavaMongoSherlockSample {
  static RxJavaSherlock createSherlock() {
    String database = "sherlock";
    MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017/" + database);
    MongoCollection<Document> collection = mongoClient
        .getDatabase("sherlock")
        .getCollection("locks");
    return ReactiveMongoSherlock.builder()
        .withMongoCollection(collection)
        .build(RxJavaSherlock::wrapReactiveSherlock);
  }

  public static void main(String[] args) {
    RxJavaSherlock sherlock = createSherlock();
    RxJavaDistributedLock lock = sherlock.createLock("sample-acquire");
    lock.acquire()
        .filter(AcquireResult::isAcquired)
        .flatMapSingle(result -> {
          System.out.println("Lock granted!");
          return lock.release();
        })
        .blockingGet();
  }
}
