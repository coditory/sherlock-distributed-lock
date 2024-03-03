package com.coditory.sherlock

import com.coditory.sherlock.base.DistributedLocksCreator
import groovy.transform.CompileStatic

@CompileStatic
interface SqlDistributedLocksCreator extends DistributedLocksCreator {
    Sherlock createSherlock(DataSourceConfigurer configurer)
}
