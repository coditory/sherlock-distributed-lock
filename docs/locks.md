There are two main questions regarding lock acquisition:

- How long the lock should stay acquired? [Lock Duration](#lock-duration)
- How to handle lock acquisition from the same instance? [Lock Types](#lock-types)

## Lock Duration

Sherlock locks are acquired for a specific duration.
Thanks to this approach when lock owning instance unexpectedly goes down,
lock is automatically released after expiration.

There are 3 methods to acquire a lock:

- `lock.acquire()` - acquires lock for a default duration (5 minutes) after which lock is automatically released
- `lock.acquire(Duration.ofMinutes(3))` - acquires a lock for a specific duration
- `lock.acquireForever()` - acquires a lock forever. This lock never expires. Use it wisely.

## Lock Types

Sherlock provides different lock types to handle different locking scenarios:

- [SingleEntrantDistributedLock](#singleentrantdistributedlock) - (default lock type) lock can be acquired only once. Even lock owner cannot acquire the lock for the second time.
- [ReentrantDistributedLock](#reentrantdistributedlock) - lock can be acquired by only one instance. Lock owner can acquire the lock for the second time.
- [OverridingDistributedLock](#overridingdistributedlock) - lock state can be overridden freely. It's for administrative purposes.


### SingleEntrantDistributedLock

Owner of a `SingleEntrantDistributedLock` cannot acquire the same lock twice. It's the default lock type.

Acquiring lock with `SingleEntrantDistributedLock`:

```java
DistributedLock lock = sherlock.createLock(lockId);

Instance A                     | Instance B
assert lock.acquire() == true; |
                               | assert lock.lock() == false
assert lock.acquire() == false;|
```

Releasing `SingleEntrantDistributedLock` (the same as `ReentrantDistributedLock`):
```java
Instance A                     | Instance B
assert lock.acquire() == true; |
                               | assert lock.lock() == false
                               | assert lock.release() == false
assert lock.release() == true; |
assert lock.release() == false;|
```

### ReentrantDistributedLock

Owner of a `ReentrantDistributedLock` can acquire the same lock multiple times

Acquiring `ReentrantDistributedLock`:
```java
DistributedLock lock = sherlock.createReentrantLock(lockId);

Instance A                     | Instance B
assert lock.acquire() == true; |
                               | assert lock.lock() == false
assert lock.acquire() == true; |
```

Releasing `ReentrantDistributedLock` (the same as `SingleEntrantDistributedLock`):
```java
Instance A                     | Instance B
assert lock.acquire() == true; |
                               | assert lock.lock() == false
                               | assert lock.release() == false
assert lock.release() == true; |
assert lock.release() == false;|
```

### OverridingDistributedLock

`OverridingDistributedLock` lock may be acquired and/or released any time.
It was created for purely administrative tasks, like releasing a lock that was blocked in acquired state. 

Acquiring a `OverridingDistributedLock`
```java
DistributedLock lock = sherlock.createOverridingLock(lockId);

Instance A                     | Instance B
assert lock.acquire() == true; |
                               | assert lock.lock() == true
assert lock.acquire() == true; |
assert lock.acquire() == true; |
```

Releasing a `OverridingDistributedLock`
```java
Instance A                     | Instance B
assert lock.acquire() == true; |
                               | assert lock.lock() == true
                               | assert lock.release() == true
assert lock.release() == false;|
assert lock.release() == false;|
```
