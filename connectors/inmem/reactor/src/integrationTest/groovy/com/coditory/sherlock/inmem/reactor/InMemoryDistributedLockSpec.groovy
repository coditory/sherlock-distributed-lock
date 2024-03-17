package com.coditory.sherlock.inmem.reactor

import com.coditory.sherlock.AcquireLockMultipleTimesSpec
import com.coditory.sherlock.AcquireLockSpec
import com.coditory.sherlock.InfiniteAcquireLockSpec
import com.coditory.sherlock.ReleaseLockSpec

class ReactorInMemoryReleaseLockSpec extends ReleaseLockSpec
        implements UsesReactorInMemorySherlock {}

class ReactorInMemoryAcquireLockSpec extends AcquireLockSpec
        implements UsesReactorInMemorySherlock {}

class ReactorInMemoryAcquireLockMultipleTimesSpec extends AcquireLockMultipleTimesSpec
        implements UsesReactorInMemorySherlock {}

class ReactorInMemoryInfiniteAcquireLockSpec extends InfiniteAcquireLockSpec
        implements UsesReactorInMemorySherlock {}
