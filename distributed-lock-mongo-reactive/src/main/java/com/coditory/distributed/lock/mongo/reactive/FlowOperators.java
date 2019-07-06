package com.coditory.distributed.lock.mongo.reactive;

import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.function.Function;

import static com.coditory.distributed.lock.mongo.reactive.FlatTransformProcessor.flatTransform;
import static com.coditory.distributed.lock.mongo.reactive.TransformProcessor.transform;

final class FlowOperators {
  static <T, R> Publisher<R> map(Publisher<T> publisher, Function<T, R> mapper) {
    TransformProcessor<T, R> processor = transform(mapper);
    publisher.subscribe(processor);
    return processor;
  }

  static <T, R> Publisher<R> flatMap(Publisher<T> publisher, Function<T, Publisher<R>> mapper) {
    FlatTransformProcessor<T, R> processor = flatTransform(mapper);
    publisher.subscribe(processor);
    return processor;
  }

  static <T> Publisher<T> emptyPublisher() {
    return Subscriber::onComplete;
  }
}
