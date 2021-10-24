# Testing

Sherlock provides stubs and mocks for testing purposes. Try it out:
`SherlockStub`, `ReactorSherlockStub`, `DistributedLockMock` and `ReactorDistributedLockMock`.

!!! info "Creating own stub and mocks"
    Sherlock API consists mostly of interfaces, so it's easy to create stubs and mocks for your own purposes.

Sample usage in spock tests:

```groovy
def "should release a lock after operation"() {
  given: "there is a released lock"
    DistributedLockMock lock = DistributedLockMock.alwaysReleasedLock()
  when: "single instance action is executed"
    boolean taskPerformed = singleInstanceAction(lock)
  then: "the task was performed"
    taskPerformed == true
  and: "lock was acquired and released"
    lock.wasAcquiredAndReleased == true
}

def "should not perform single instance action when lock is locked"() {
  given: "there is a lock acquired by other instance"
    DistributedLockMock lock = DistributedLockMock.alwaysAcquiredLock()
  when: "single instance action is executed"
    boolean taskPerformed = singleInstanceAction(lock)
  then: "action did not perform the task"
    taskPerformed == false
  and: "action failed acquiring the lock"
    lock.wasAcquireRejected == true
  and: "action did not release the lock"
    lock.wasReleaseInvoked == false
}
```

!!! info "In Memory Connector"
    The easiest way to setup Sherlock in tests is to use [In-Memory Connector](connectors/inmem.md).
    Use stubs when you need more control over the locking mechanism.
