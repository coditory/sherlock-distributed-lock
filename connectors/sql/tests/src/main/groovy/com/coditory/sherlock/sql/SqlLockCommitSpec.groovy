package com.coditory.sherlock.sql

import com.coditory.sherlock.DistributedLock
import com.coditory.sherlock.LocksBaseSpec
import com.coditory.sherlock.Sherlock
import spock.lang.Unroll

abstract class SqlLockCommitSpec extends LocksBaseSpec implements SqlDistributedLocksCreator {
    @Unroll
    def "should preserve commit lock with dataSource auto-commit=#autoCommit"() {
        given:
            Sherlock sherlock = createSherlock({
                it.setAutoCommit(autoCommit)
                it.setMaximumPoolSize(2)
            })
            DistributedLock lock = sherlock.createLock("lock")
        when:
            boolean firstResult = lock.acquire()
            boolean secondResult = lock.acquire()
        then:
            firstResult == true
            secondResult == false
        where:
            autoCommit << [true, false]
    }
}
