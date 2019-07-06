package com.coditory.distributed.lock.mongo

import com.coditory.distributed.lock.tests.AcquireLockMultipleTimesSpec
import com.coditory.distributed.lock.tests.AcquireLockSpec
import com.coditory.distributed.lock.tests.InfiniteAcquireLockSpec
import com.coditory.distributed.lock.tests.ReleaseLockSpec

class MongoReleaseLockSpec extends ReleaseLockSpec implements UsesMongoDistributedLocks {}
class MongoAcquireLockSpec extends AcquireLockSpec implements UsesMongoDistributedLocks {}
class MongoAcquireLockMultipleTimesSpec extends AcquireLockMultipleTimesSpec implements UsesMongoDistributedLocks {}
class MongoInfiniteAcquireLockSpec extends InfiniteAcquireLockSpec implements UsesMongoDistributedLocks {}
