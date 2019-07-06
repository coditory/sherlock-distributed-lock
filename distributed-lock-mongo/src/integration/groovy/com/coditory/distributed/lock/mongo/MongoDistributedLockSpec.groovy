package com.coditory.distributed.lock.mongo

import com.coditory.distributed.lock.tests.AcquireLockMultipleTimesSpec
import com.coditory.distributed.lock.tests.AcquireLockSpec
import com.coditory.distributed.lock.tests.InfiniteAcquireLockSpec
import com.coditory.distributed.lock.tests.ReleaseLockSpec

class MongoReleaseLockSpec extends ReleaseLockSpec implements UsesMongoLockDriver {}
class MongoAcquireLockSpec extends AcquireLockSpec implements UsesMongoLockDriver {}
class MongoAcquireLockMultipleTimesSpec extends AcquireLockMultipleTimesSpec implements UsesMongoLockDriver {}
class MongoInfiniteAcquireLockSpec extends InfiniteAcquireLockSpec implements UsesMongoLockDriver {}
