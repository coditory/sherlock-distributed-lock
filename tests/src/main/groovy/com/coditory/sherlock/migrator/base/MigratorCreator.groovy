package com.coditory.sherlock.migrator.base

import com.coditory.sherlock.Sherlock
import com.coditory.sherlock.base.DistributedLocksCreator
import groovy.transform.CompileStatic

@CompileStatic
interface MigratorCreator extends DistributedLocksCreator {
    BlockingMigratorBuilder createMigratorBuilder(Sherlock sherlock)
}