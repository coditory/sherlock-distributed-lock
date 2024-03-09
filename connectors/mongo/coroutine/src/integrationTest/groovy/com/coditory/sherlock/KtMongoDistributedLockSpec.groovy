package com.coditory.sherlock

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
