package com.coditory.sherlock

import com.coditory.sherlock.tests.AcquireLockMultipleTimesSpec
import com.coditory.sherlock.tests.AcquireLockSpec
import com.coditory.sherlock.tests.InfiniteAcquireLockSpec
import com.coditory.sherlock.tests.ReleaseLockSpec
import spock.lang.Unroll

import static com.coditory.sherlock.tests.base.LockTypes.REENTRANT
import static com.coditory.sherlock.tests.base.LockTypes.SINGLE_ENTRANT

class InMemoryReleaseLockSpec extends ReleaseLockSpec implements UsesInMemorySherlock {}
class InMemoryAcquireLockSpec extends AcquireLockSpec implements UsesInMemorySherlock {}
class InMemoryAcquireLockMultipleTimesSpec extends AcquireLockMultipleTimesSpec implements UsesInMemorySherlock {}
class InMemoryInfiniteAcquireLockSpec extends InfiniteAcquireLockSpec implements UsesInMemorySherlock {}
