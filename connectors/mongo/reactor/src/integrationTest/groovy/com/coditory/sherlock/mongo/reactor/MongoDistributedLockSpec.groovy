package com.coditory.sherlock.mongo.reactor


import com.coditory.sherlock.AcquireLockMultipleTimesSpec
import com.coditory.sherlock.AcquireLockSpec
import com.coditory.sherlock.HandleDbFailureSpec
import com.coditory.sherlock.InfiniteAcquireLockSpec
import com.coditory.sherlock.ReleaseLockSpec

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
