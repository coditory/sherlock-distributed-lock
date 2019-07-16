package com.coditory.sherlock.client;

import com.coditory.sherlock.reactive.ReactiveMongoSherlock;
import com.coditory.sherlock.reactive.connector.AcquireResult;
import com.coditory.sherlock.reactor.ReactorDistributedLock;
import com.coditory.sherlock.reactor.ReactorSherlock;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;
import org.bson.Document;

public class ReactiveMongoSherlockSample {
  static ReactorSherlock createSherlock() {
    String database = "sherlock";
    MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017/" + database);
    MongoCollection<Document> collection = mongoClient
        .getDatabase("sherlock")
        .getCollection("locks");
    return ReactiveMongoSherlock.builder()
        .withMongoCollection(collection)
        .build(ReactorSherlock::wrapReactiveSherlock);
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
