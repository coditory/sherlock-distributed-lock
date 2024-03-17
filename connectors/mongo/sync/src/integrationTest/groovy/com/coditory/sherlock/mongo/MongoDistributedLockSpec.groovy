package com.coditory.sherlock.mongo

import com.coditory.sherlock.AcquireLockMultipleTimesSpec
import com.coditory.sherlock.AcquireLockSpec
import com.coditory.sherlock.HandleDbFailureSpec
import com.coditory.sherlock.InfiniteAcquireLockSpec
import com.coditory.sherlock.ReleaseLockSpec

class MongoReleaseLockSpec extends ReleaseLockSpec
        implements UsesMongoSherlock {}

class MongoAcquireLockSpec extends AcquireLockSpec
        implements UsesMongoSherlock {}

class MongoAcquireLockMultipleTimesSpec extends AcquireLockMultipleTimesSpec
        implements UsesMongoSherlock {}

class MongoInfiniteAcquireLockSpec extends InfiniteAcquireLockSpec
        implements UsesMongoSherlock {}

class MongoHandleDbFailureSpec extends HandleDbFailureSpec
        implements UsesMongoSherlock {}
