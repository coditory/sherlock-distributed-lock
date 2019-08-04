package com.coditory.sherlock

import com.coditory.sherlock.base.UsesRxSherlock

/*
 * Run a set of tests on any connector to prove that reactor mapping is correct
 */
class RxInMemoryReleaseLockSpec extends ReleaseLockSpec implements UsesRxSherlock {}
class RxInMemoryAcquireLockSpec extends AcquireLockSpec implements UsesRxSherlock {}
class RxInMemoryAcquireLockMultipleTimesSpec extends AcquireLockMultipleTimesSpec implements UsesRxSherlock {}
class RxInMemoryInfiniteAcquireLockSpec extends InfiniteAcquireLockSpec implements UsesRxSherlock {}
