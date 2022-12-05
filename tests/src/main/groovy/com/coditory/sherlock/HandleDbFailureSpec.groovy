package com.coditory.sherlock

import com.coditory.sherlock.base.DatabaseManager
import com.coditory.sherlock.base.LockAssertions
import com.coditory.sherlock.base.LockTypes

abstract class HandleDbFailureSpec extends LocksBaseSpec implements LockAssertions, DatabaseManager {
    String lockId = "acquire-id"
    String instanceId = "instance-id"

    def "should reconnect after DB failure"() {
        given:
            sherlock.initialize()
            DistributedLock lock = createLock(LockTypes.SINGLE_ENTRANT, lockId, instanceId)
        when:
            stopDatabase()
            lock.acquire()
        then:
            SherlockException e = thrown(SherlockException)
            e.getMessage().startsWith("Could not acquire lock")

        when:
            startDatabase()
            boolean result = lock.acquire()
        then:
            result == true
    }
}
