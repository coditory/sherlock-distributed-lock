package com.coditory.sherlock.rxjava

import com.coditory.sherlock.rxjava.base.UsesRxJavaSherlock
import com.coditory.sherlock.tests.AcquireLockMultipleTimesSpec
import com.coditory.sherlock.tests.AcquireLockSpec
import com.coditory.sherlock.tests.InfiniteAcquireLockSpec
import com.coditory.sherlock.tests.ReleaseLockSpec

/*
 * Run a set of tests on any connector to prove that reactor mapping is correct
 */
class RxJavaInMemoryReleaseLockSpec extends ReleaseLockSpec implements UsesRxJavaSherlock {}
class RxJavaInMemoryAcquireLockSpec extends AcquireLockSpec implements UsesRxJavaSherlock {}
class RxJavaInMemoryAcquireLockMultipleTimesSpec extends AcquireLockMultipleTimesSpec implements UsesRxJavaSherlock {}
class RxJavaInMemoryInfiniteAcquireLockSpec extends InfiniteAcquireLockSpec implements UsesRxJavaSherlock {}
