package com.coditory.sherlock.reactive;

@FunctionalInterface
public interface ReactiveSherlockApiWrapper<T> {
  T wrapApi(ReactiveSherlock reactiveSherlock);
}
