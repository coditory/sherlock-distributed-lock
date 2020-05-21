package com.coditory.sherlock;

import io.reactivex.Single;
import org.reactivestreams.FlowAdapters;

import java.util.concurrent.Flow.Publisher;

final class PublisherToSingleConverter {
    static <T> Single<T> convertToSingle(Publisher<T> publisher) {
        return Single.fromPublisher(FlowAdapters.toPublisher(publisher));
    }
}
