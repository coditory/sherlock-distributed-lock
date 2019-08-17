# Lock Duration

There are 3 methods for acquiring a lock:

- `lock.acquire()` - acquires lock for a default duration (5 minutes) after which lock is automatically released
- `lock.acquire(Duration.ofMinutes(3))` - acquires a lock for a specific duration
- `lock.acquireForever()` - acquires a lock forever. Use it wisely.
