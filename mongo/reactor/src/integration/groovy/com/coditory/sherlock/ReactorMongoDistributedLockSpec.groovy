package com.coditory.sherlock

class ReactorMongoReleaseLockSpec extends ReleaseLockSpec
        implements UsesReactorMongoSherlock {}

class ReactorMongoAcquireLockSpec extends AcquireLockSpec
        implements UsesReactorMongoSherlock {}

class ReactorMongoAcquireLockMultipleTimesSpec extends AcquireLockMultipleTimesSpec
        implements UsesReactorMongoSherlock {}

class ReactorMongoInfiniteAcquireLockSpec extends InfiniteAcquireLockSpec
        implements UsesReactorMongoSherlock {}

class ReactorMongoHandleDbFailureSpec extends HandleDbFailureSpec
        implements UsesReactorMongoSherlock {}
