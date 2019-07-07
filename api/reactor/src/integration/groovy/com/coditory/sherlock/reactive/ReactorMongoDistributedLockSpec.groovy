package com.coditory.sherlock.reactive

import com.coditory.sherlock.tests.AcquireLockMultipleTimesSpec
import com.coditory.sherlock.tests.AcquireLockSpec
import com.coditory.sherlock.tests.InfiniteAcquireLockSpec
import com.coditory.sherlock.tests.ReleaseLockSpec

/*
 * Run a set of tests on any driver to prove that reactor mapping is correct
 */

class ReactorMongoReleaseLockSpec extends ReleaseLockSpec implements UsesReactorSherlock {}

class ReactorMongoAcquireLockSpec extends AcquireLockSpec implements UsesReactorSherlock {}

class ReactorMongoAcquireLockMultipleTimesSpec extends AcquireLockMultipleTimesSpec implements UsesReactorSherlock {}

class ReactorMongoInfiniteAcquireLockSpec extends InfiniteAcquireLockSpec implements UsesReactorSherlock {}
