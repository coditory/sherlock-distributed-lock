package com.coditory.sherlock

import com.coditory.sherlock.base.UsesKtSherlock

class KtInMemoryReleaseLockSpec extends ReleaseLockSpec
        implements UsesKtSherlock {}

class KtInMemoryAcquireLockSpec extends AcquireLockSpec
        implements UsesKtSherlock {}

class KtInMemoryAcquireLockMultipleTimesSpec extends AcquireLockMultipleTimesSpec
        implements UsesKtSherlock {}

class KtInMemoryInfiniteAcquireLockSpec extends InfiniteAcquireLockSpec
        implements UsesKtSherlock {}
