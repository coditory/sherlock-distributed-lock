package com.coditory.sherlock.base

import groovy.transform.CompileStatic

@CompileStatic
interface DatabaseManager {
    void stopDatabase()

    void startDatabase()
}
