package com.coditory.sherlock

class ReactiveInMemoryReleaseLockSpec extends ReleaseLockSpec
        implements UsesReactiveInMemorySherlock {}

class ReactiveInMemoryAcquireLockSpec extends AcquireLockSpec
        implements UsesReactiveInMemorySherlock {}

class ReactiveInMemoryAcquireLockMultipleTimesSpec extends AcquireLockMultipleTimesSpec
        implements UsesReactiveInMemorySherlock {}

class ReactiveInMemoryInfiniteAcquireLockSpec extends InfiniteAcquireLockSpec
        implements UsesReactiveInMemorySherlock {}
