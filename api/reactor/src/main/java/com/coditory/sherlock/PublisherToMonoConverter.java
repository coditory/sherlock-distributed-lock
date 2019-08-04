package com.coditory.sherlock;

import reactor.core.publisher.Mono;

import java.util.concurrent.Flow.Publisher;

import static reactor.adapter.JdkFlowAdapter.flowPublisherToFlux;

final class PublisherToMonoConverter {
  static <T> Mono<T> convertToMono(Publisher<T> publisher) {
    return flowPublisherToFlux(publisher)
      .single();
  }
}
