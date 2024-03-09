package com.coditory.sherlock

class KtInMemoryReleaseLockSpec extends ReleaseLockSpec
        implements UsesKtInMemorySherlock {}

class KtInMemoryAcquireLockSpec extends AcquireLockSpec
        implements UsesKtInMemorySherlock {}

class KtInMemoryAcquireLockMultipleTimesSpec extends AcquireLockMultipleTimesSpec
        implements UsesKtInMemorySherlock {}

class KtInMemoryInfiniteAcquireLockSpec extends InfiniteAcquireLockSpec
        implements UsesKtInMemorySherlock {}
