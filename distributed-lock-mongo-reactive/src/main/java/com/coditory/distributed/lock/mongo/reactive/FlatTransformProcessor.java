package com.coditory.distributed.lock.mongo.reactive;

import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.SubmissionPublisher;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.coditory.distributed.lock.common.util.Preconditions.expectNonNull;

final class FlatTransformProcessor<T, R> extends SubmissionPublisher<R>
    implements Flow.Processor<T, R> {
  static <T, R> FlatTransformProcessor<T, R> flatTransform(Function<T, Publisher<R>> transform) {
    return new FlatTransformProcessor<>(transform);
  }

  private final Function<T, Publisher<R>> transform;
  private Flow.Subscription subscription;

  private FlatTransformProcessor(Function<T, Publisher<R>> transform) {
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
    transform.apply(item)
        .subscribe(new PassingSubscriber<>(
            this::submit,
            () -> subscription.request(1),
            this::onError
        ));
  }

  @Override
  public void onError(Throwable throwable) {
    closeExceptionally(throwable);
  }

  @Override
  public void onComplete() {
    close();
  }

  static class PassingSubscriber<T> implements Subscriber<T> {
    private final Runnable passingOnComplete;
    private final Consumer<Throwable> passingOnError;
    private final Consumer<T> passingOnNext;
    private Flow.Subscription subscription;

    PassingSubscriber(
        Consumer<T> onNext,
        Runnable onComplete,
        Consumer<Throwable> onError) {
      this.passingOnComplete = onComplete;
      this.passingOnError = onError;
      this.passingOnNext = onNext;
    }

    @Override
    public void onSubscribe(Subscription subscription) {
      if (this.subscription != null) {
        throw new IllegalStateException("Already subscribed");
      }
      this.subscription = subscription;
      subscription.request(1);
    }

    @Override
    public void onNext(T item) {
      passingOnNext.accept(item);
      subscription.request(1);
    }

    @Override
    public void onError(Throwable throwable) {
      passingOnError.accept(throwable);
    }

    @Override
    public void onComplete() {
      passingOnComplete.run();
    }
  }
}
