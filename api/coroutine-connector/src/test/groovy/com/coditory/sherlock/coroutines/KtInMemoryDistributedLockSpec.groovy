package com.coditory.sherlock.coroutines

import com.coditory.sherlock.AcquireLockMultipleTimesSpec
import com.coditory.sherlock.AcquireLockSpec
import com.coditory.sherlock.InfiniteAcquireLockSpec
import com.coditory.sherlock.ReleaseLockSpec
import com.coditory.sherlock.coroutines.base.UsesKtSherlock

class KtInMemoryReleaseLockSpec extends ReleaseLockSpec
        implements UsesKtSherlock {}

class KtInMemoryAcquireLockSpec extends AcquireLockSpec
        implements UsesKtSherlock {}

class KtInMemoryAcquireLockMultipleTimesSpec extends AcquireLockMultipleTimesSpec
        implements UsesKtSherlock {}

class KtInMemoryInfiniteAcquireLockSpec extends InfiniteAcquireLockSpec
        implements UsesKtSherlock {}
