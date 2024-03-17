package com.coditory.sherlock.sql

import com.coditory.sherlock.Sherlock
import com.coditory.sherlock.base.DistributedLocksCreator
import groovy.transform.CompileStatic

@CompileStatic
interface SqlDistributedLocksCreator extends DistributedLocksCreator {
    Sherlock createSherlock(DataSourceConfigurer configurer)
}
