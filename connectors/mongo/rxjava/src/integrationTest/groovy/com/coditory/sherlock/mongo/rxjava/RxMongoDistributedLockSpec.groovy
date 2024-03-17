package com.coditory.sherlock.mongo.rxjava

import com.coditory.sherlock.AcquireLockMultipleTimesSpec
import com.coditory.sherlock.AcquireLockSpec
import com.coditory.sherlock.HandleDbFailureSpec
import com.coditory.sherlock.InfiniteAcquireLockSpec
import com.coditory.sherlock.ReleaseLockSpec

class RxMongoReleaseLockSpec extends ReleaseLockSpec
        implements UsesRxMongoSherlock {}

class RxMongoAcquireLockSpec extends AcquireLockSpec
        implements UsesRxMongoSherlock {}

class RxMongoAcquireLockMultipleTimesSpec extends AcquireLockMultipleTimesSpec
        implements UsesRxMongoSherlock {}

class RxMongoInfiniteAcquireLockSpec extends InfiniteAcquireLockSpec
        implements UsesRxMongoSherlock {}

class RxMongoHandleDbFailureSpec extends HandleDbFailureSpec
        implements UsesRxMongoSherlock {}
