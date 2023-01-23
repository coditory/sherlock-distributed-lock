package com.coditory.sherlock


import org.reactivestreams.Publisher
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription

import java.util.concurrent.TimeUnit

import static org.awaitility.Awaitility.await

class PublisherSubscriber<T> implements Subscriber<T> {
    static <T> List<T> consumeAll(Publisher<T> publisher) {
        PublisherSubscriber<T> subscriber = new PublisherSubscriber<>()
        publisher.subscribe(subscriber)
        return subscriber.awaitCompletion()
    }

    static <T> T consumeFirst(Publisher<T> publisher) {
        List<T> results = consumeAll(publisher)
        return results.size() > 0 ? results.first() : null
    }

    private volatile boolean completed
    private volatile Throwable error
    private Subscription subscription
    public List<T> consumedElements = new LinkedList<>()

    @Override
    synchronized void onSubscribe(Subscription subscription) {
        this.subscription = subscription
        subscription.request(1)
    }

    @Override
    synchronized void onNext(T item) {
        consumedElements.add(item)
        subscription.request(1)
    }

    @Override
    synchronized void onError(Throwable t) {
        error = t
    }

    @Override
    synchronized void onComplete() {
        completed = true
    }

    synchronized T getFirst() {
        if (hasError()) {
            throw getError()
        }
        if (!isCompleted()) {
            throw new IllegalStateException("Subscriber did not complete")
        }
        return consumedElements.get(0)
    }

    synchronized boolean isCompleted() {
        return completed || error != null
    }

    synchronized boolean hasError() {
        return error != null
    }

    synchronized Throwable getError() {
        return error
    }

    List<T> awaitCompletion() {
        await().atMost(3000, TimeUnit.MILLISECONDS)
                .until(this::isCompleted)
        return List.copyOf(consumedElements)
    }
}
