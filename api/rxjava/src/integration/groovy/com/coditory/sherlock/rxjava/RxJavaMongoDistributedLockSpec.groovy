package com.coditory.sherlock.rxjava

import com.coditory.sherlock.rxjava.base.UsesRxJavaSherlock
import com.coditory.sherlock.tests.AcquireLockMultipleTimesSpec
import com.coditory.sherlock.tests.AcquireLockSpec
import com.coditory.sherlock.tests.InfiniteAcquireLockSpec
import com.coditory.sherlock.tests.ReleaseLockSpec

/*
 * Run a set of tests on any connector to prove that reactor mapping is correct
 */

class RxJavaMongoReleaseLockSpec extends ReleaseLockSpec implements UsesRxJavaSherlock {}

class RxJavaMongoAcquireLockSpec extends AcquireLockSpec implements UsesRxJavaSherlock {}

class RxJavaMongoAcquireLockMultipleTimesSpec extends AcquireLockMultipleTimesSpec implements UsesRxJavaSherlock {}

class RxJavaMongoInfiniteAcquireLockSpec extends InfiniteAcquireLockSpec implements UsesRxJavaSherlock {}
