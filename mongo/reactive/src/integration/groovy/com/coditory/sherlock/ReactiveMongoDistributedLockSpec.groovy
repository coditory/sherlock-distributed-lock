package com.coditory.sherlock

class ReactiveMongoReleaseLockSpec extends ReleaseLockSpec
        implements UsesReactiveMongoSherlock {}

class ReactiveMongoAcquireLockSpec extends AcquireLockSpec
        implements UsesReactiveMongoSherlock {}

class ReactiveMongoAcquireLockMultipleTimesSpec extends AcquireLockMultipleTimesSpec
        implements UsesReactiveMongoSherlock {}

class ReactiveMongoInfiniteAcquireLockSpec extends InfiniteAcquireLockSpec
        implements UsesReactiveMongoSherlock {}

class ReactiveMongoHandleDbFailureSpec extends HandleDbFailureSpec
        implements UsesReactiveMongoSherlock {}