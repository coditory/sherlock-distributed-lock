package com.coditory.sherlock

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
