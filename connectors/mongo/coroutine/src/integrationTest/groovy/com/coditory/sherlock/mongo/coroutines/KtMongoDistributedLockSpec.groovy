package com.coditory.sherlock.mongo.coroutines

import com.coditory.sherlock.AcquireLockMultipleTimesSpec
import com.coditory.sherlock.AcquireLockSpec
import com.coditory.sherlock.HandleDbFailureSpec
import com.coditory.sherlock.InfiniteAcquireLockSpec
import com.coditory.sherlock.ReleaseLockSpec

class KtMongoReleaseLockSpec extends ReleaseLockSpec
        implements UsesKtMongoSherlock {}

class KtMongoAcquireLockSpec extends AcquireLockSpec
        implements UsesKtMongoSherlock {}

class KtMongoAcquireLockMultipleTimesSpec extends AcquireLockMultipleTimesSpec
        implements UsesKtMongoSherlock {}

class KtMongoInfiniteAcquireLockSpec extends InfiniteAcquireLockSpec
        implements UsesKtMongoSherlock {}

class KtMongoHandleDbFailureSpec extends HandleDbFailureSpec
        implements UsesKtMongoSherlock {}
