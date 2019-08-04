package com.coditory.sherlock

class MongoReleaseLockSpec extends ReleaseLockSpec implements UsesMongoSherlock {}
class MongoAcquireLockSpec extends AcquireLockSpec implements UsesMongoSherlock {}
class MongoAcquireLockMultipleTimesSpec extends AcquireLockMultipleTimesSpec implements UsesMongoSherlock {}
class MongoInfiniteAcquireLockSpec extends InfiniteAcquireLockSpec implements UsesMongoSherlock {}
