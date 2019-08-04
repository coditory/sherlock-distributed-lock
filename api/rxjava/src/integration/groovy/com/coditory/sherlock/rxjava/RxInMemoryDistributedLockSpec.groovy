package com.coditory.sherlock.rxjava

import com.coditory.sherlock.rxjava.base.UsesRxSherlock
import com.coditory.sherlock.tests.AcquireLockMultipleTimesSpec
import com.coditory.sherlock.tests.AcquireLockSpec
import com.coditory.sherlock.tests.InfiniteAcquireLockSpec
import com.coditory.sherlock.tests.ReleaseLockSpec

/*
 * Run a set of tests on any connector to prove that reactor mapping is correct
 */
class RxInMemoryReleaseLockSpec extends ReleaseLockSpec implements UsesRxSherlock {}
class RxInMemoryAcquireLockSpec extends AcquireLockSpec implements UsesRxSherlock {}
class RxInMemoryAcquireLockMultipleTimesSpec extends AcquireLockMultipleTimesSpec implements UsesRxSherlock {}
class RxInMemoryInfiniteAcquireLockSpec extends InfiniteAcquireLockSpec implements UsesRxSherlock {}
