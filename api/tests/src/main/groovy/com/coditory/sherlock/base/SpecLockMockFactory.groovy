package com.coditory.sherlock.base

interface SpecLockMockFactory {
  SpecDistributedLockMock releasedInMemoryLock();

  SpecDistributedLockMock acquiredInMemoryLock();

  SpecDistributedLockMock releasedInMemoryLock(String lockId);

  SpecDistributedLockMock acquiredInMemoryLock(String lockId);

  SpecDistributedLockMock inMemoryLock(String lockId, boolean state);

  SpecDistributedLockMock releasedReentrantInMemoryLock();

  SpecDistributedLockMock acquiredReentrantInMemoryLock();

  SpecDistributedLockMock releasedReentrantInMemoryLock(String lockId);

  SpecDistributedLockMock acquiredReentrantInMemoryLock(String lockId);

  SpecDistributedLockMock lockStub(boolean result);

  SpecDistributedLockMock lockStub(boolean acquireResult, boolean releaseResult);

  SpecDistributedLockMock lockStub(String lockId, boolean result);

  SpecDistributedLockMock lockStub(String lockId, boolean acquireResult, boolean releaseResult);

  SpecDistributedLockMock sequencedLock(List<Boolean> acquireResults, List<Boolean> releaseResults);

  SpecDistributedLockMock sequencedLock(String lockId, List<Boolean> acquireResults, List<Boolean> releaseResults)
}
