package com.coditory.sherlock.rxjava

import com.coditory.sherlock.AcquireLockMultipleTimesSpec
import com.coditory.sherlock.AcquireLockSpec
import com.coditory.sherlock.InfiniteAcquireLockSpec
import com.coditory.sherlock.ReleaseLockSpec
import com.coditory.sherlock.rxjava.base.UsesReactorSherlock

class ReactorInMemoryReleaseLockSpec extends ReleaseLockSpec
        implements UsesReactorSherlock {}

class ReactorInMemoryAcquireLockSpec extends AcquireLockSpec
        implements UsesReactorSherlock {}

class ReactorInMemoryAcquireLockMultipleTimesSpec extends AcquireLockMultipleTimesSpec
        implements UsesReactorSherlock {}

class ReactorInMemoryInfiniteAcquireLockSpec extends InfiniteAcquireLockSpec
        implements UsesReactorSherlock {}
