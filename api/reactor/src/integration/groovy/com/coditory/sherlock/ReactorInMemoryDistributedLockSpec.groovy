package com.coditory.sherlock

import com.coditory.sherlock.base.UsesReactorSherlock

/*
 * Run a set of tests on any connector to prove that reactor mapping is correct
 */

class ReactorInMemoryReleaseLockSpec extends ReleaseLockSpec implements UsesReactorSherlock {}

class ReactorInMemoryAcquireLockSpec extends AcquireLockSpec implements UsesReactorSherlock {}

class ReactorInMemoryAcquireLockMultipleTimesSpec extends AcquireLockMultipleTimesSpec implements UsesReactorSherlock {}

class ReactorInMemoryInfiniteAcquireLockSpec extends InfiniteAcquireLockSpec implements UsesReactorSherlock {}
