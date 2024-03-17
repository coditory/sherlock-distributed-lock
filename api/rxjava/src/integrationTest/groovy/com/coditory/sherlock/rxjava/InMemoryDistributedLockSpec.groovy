package com.coditory.sherlock.rxjava

import com.coditory.sherlock.AcquireLockMultipleTimesSpec
import com.coditory.sherlock.AcquireLockSpec
import com.coditory.sherlock.InfiniteAcquireLockSpec
import com.coditory.sherlock.ReleaseLockSpec
import com.coditory.sherlock.rxjava.base.UsesRxSherlock

class RxInMemoryReleaseLockSpec extends ReleaseLockSpec
        implements UsesRxSherlock {}

class RxInMemoryAcquireLockSpec extends AcquireLockSpec
        implements UsesRxSherlock {}

class RxInMemoryAcquireLockMultipleTimesSpec extends AcquireLockMultipleTimesSpec
        implements UsesRxSherlock {}

class RxInMemoryInfiniteAcquireLockSpec extends InfiniteAcquireLockSpec
        implements UsesRxSherlock {}
