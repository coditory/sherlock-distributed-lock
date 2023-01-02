package com.coditory.sherlock

class ReactorInMemoryReleaseLockSpec extends ReleaseLockSpec
    implements UsesReactorInMemorySherlock {}

class ReactorInMemoryAcquireLockSpec extends AcquireLockSpec
    implements UsesReactorInMemorySherlock {}

class ReactorInMemoryAcquireLockMultipleTimesSpec extends AcquireLockMultipleTimesSpec
    implements UsesReactorInMemorySherlock {}

class ReactorInMemoryInfiniteAcquireLockSpec extends InfiniteAcquireLockSpec
    implements UsesReactorInMemorySherlock {}
