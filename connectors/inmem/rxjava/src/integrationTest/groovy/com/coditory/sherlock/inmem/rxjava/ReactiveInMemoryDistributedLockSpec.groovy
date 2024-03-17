package com.coditory.sherlock.inmem.rxjava

import com.coditory.sherlock.AcquireLockMultipleTimesSpec
import com.coditory.sherlock.AcquireLockSpec
import com.coditory.sherlock.InfiniteAcquireLockSpec
import com.coditory.sherlock.ReleaseLockSpec

class ReactiveInMemoryReleaseLockSpec extends ReleaseLockSpec
        implements UsesReactiveInMemorySherlock {}

class ReactiveInMemoryAcquireLockSpec extends AcquireLockSpec
        implements UsesReactiveInMemorySherlock {}

class ReactiveInMemoryAcquireLockMultipleTimesSpec extends AcquireLockMultipleTimesSpec
        implements UsesReactiveInMemorySherlock {}

class ReactiveInMemoryInfiniteAcquireLockSpec extends InfiniteAcquireLockSpec
        implements UsesReactiveInMemorySherlock {}
