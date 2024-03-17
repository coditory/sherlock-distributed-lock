package com.coditory.sherlock.inmem.coroutines

import com.coditory.sherlock.AcquireLockMultipleTimesSpec
import com.coditory.sherlock.AcquireLockSpec
import com.coditory.sherlock.InfiniteAcquireLockSpec
import com.coditory.sherlock.ReleaseLockSpec

class KtInMemoryReleaseLockSpec extends ReleaseLockSpec
        implements UsesKtInMemorySherlock {}

class KtInMemoryAcquireLockSpec extends AcquireLockSpec
        implements UsesKtInMemorySherlock {}

class KtInMemoryAcquireLockMultipleTimesSpec extends AcquireLockMultipleTimesSpec
        implements UsesKtInMemorySherlock {}

class KtInMemoryInfiniteAcquireLockSpec extends InfiniteAcquireLockSpec
        implements UsesKtInMemorySherlock {}
