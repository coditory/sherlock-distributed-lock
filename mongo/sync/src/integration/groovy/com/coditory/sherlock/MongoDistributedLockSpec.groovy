package com.coditory.sherlock


import com.coditory.sherlock.tests.AcquireLockMultipleTimesSpec
import com.coditory.sherlock.tests.AcquireLockSpec
import com.coditory.sherlock.tests.InfiniteAcquireLockSpec
import com.coditory.sherlock.tests.ReleaseLockSpec

class MongoReleaseLockSpec extends ReleaseLockSpec implements UsesMongoSherlock {}
class MongoAcquireLockSpec extends AcquireLockSpec implements UsesMongoSherlock {}
class MongoAcquireLockMultipleTimesSpec extends AcquireLockMultipleTimesSpec implements UsesMongoSherlock {}
class MongoInfiniteAcquireLockSpec extends InfiniteAcquireLockSpec implements UsesMongoSherlock {}
