package com.coditory.sherlock

import com.coditory.sherlock.base.DatabaseManager
import com.coditory.sherlock.base.UsesReactorSherlock

class ReactorInMemoryReleaseLockSpec extends ReleaseLockSpec
        implements UsesReactorSherlock {}

class ReactorInMemoryAcquireLockSpec extends AcquireLockSpec
        implements UsesReactorSherlock {}

class ReactorInMemoryAcquireLockMultipleTimesSpec extends AcquireLockMultipleTimesSpec
        implements UsesReactorSherlock {}

class ReactorInMemoryInfiniteAcquireLockSpec extends InfiniteAcquireLockSpec
        implements UsesReactorSherlock {}
