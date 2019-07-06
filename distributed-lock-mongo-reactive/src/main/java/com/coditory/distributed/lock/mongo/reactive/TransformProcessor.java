package com.coditory.distributed.lock.mongo.reactive;

import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.function.Function;

import static com.coditory.distributed.lock.common.util.Preconditions.expectNonNull;

final class TransformProcessor<T, R> extends SubmissionPublisher<R>
    implements Flow.Processor<T, R> {
  static <T, R> TransformProcessor<T, R> transform(Function<T, R> transform) {
    return new TransformProcessor<>(transform);
  }

  private final Function<T, R> transform;
  private Flow.Subscription subscription;

  private TransformProcessor(Function<T, R> transform) {
    super();
    this.transform = expectNonNull(transform);
  }

  @Override
  public void onSubscribe(Flow.Subscription subscription) {
    if (this.subscription != null) {
      throw new IllegalStateException("Already subscribed");
    }
    this.subscription = subscription;
    subscription.request(1);
  }

  @Override
  public void onNext(T item) {
    submit(transform.apply(item));
    subscription.request(1);
  }

  @Override
  public void onError(Throwable error) {
    closeExceptionally(error);
  }

  @Override
  public void onComplete() {
    close();
  }
}
