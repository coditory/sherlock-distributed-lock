package com.coditory.sherlock

class InMemoryReleaseLockSpec extends ReleaseLockSpec implements UsesInMemorySherlock {}

class InMemoryAcquireLockSpec extends AcquireLockSpec implements UsesInMemorySherlock {}

class InMemoryAcquireLockMultipleTimesSpec extends AcquireLockMultipleTimesSpec implements UsesInMemorySherlock {}

class InMemoryInfiniteAcquireLockSpec extends InfiniteAcquireLockSpec implements UsesInMemorySherlock {}
