package com.coditory.sherlock.inmem

import com.coditory.sherlock.AcquireLockMultipleTimesSpec
import com.coditory.sherlock.AcquireLockSpec
import com.coditory.sherlock.InfiniteAcquireLockSpec
import com.coditory.sherlock.ReleaseLockSpec

class InMemoryReleaseLockSpec extends ReleaseLockSpec
        implements UsesInMemorySherlock {}

class InMemoryAcquireLockSpec extends AcquireLockSpec
        implements UsesInMemorySherlock {}

class InMemoryAcquireLockMultipleTimesSpec extends AcquireLockMultipleTimesSpec
        implements UsesInMemorySherlock {}

class InMemoryInfiniteAcquireLockSpec extends InfiniteAcquireLockSpec
        implements UsesInMemorySherlock {}
