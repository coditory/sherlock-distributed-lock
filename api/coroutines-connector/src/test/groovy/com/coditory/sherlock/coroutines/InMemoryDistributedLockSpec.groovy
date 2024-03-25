package com.coditory.sherlock.coroutines

import com.coditory.sherlock.AcquireLockMultipleTimesSpec
import com.coditory.sherlock.AcquireLockSpec
import com.coditory.sherlock.InfiniteAcquireLockSpec
import com.coditory.sherlock.ReleaseLockSpec
import com.coditory.sherlock.coroutines.base.UsesKtSherlock

class CoroutinesInMemoryReleaseLockSpec extends ReleaseLockSpec
    implements UsesKtSherlock {}

class CoroutinesInMemoryAcquireLockSpec extends AcquireLockSpec
    implements UsesKtSherlock {}

class CoroutinesInMemoryAcquireLockMultipleTimesSpec extends AcquireLockMultipleTimesSpec
    implements UsesKtSherlock {}

class CoroutinesInMemoryInfiniteAcquireLockSpec extends InfiniteAcquireLockSpec
    implements UsesKtSherlock {}

