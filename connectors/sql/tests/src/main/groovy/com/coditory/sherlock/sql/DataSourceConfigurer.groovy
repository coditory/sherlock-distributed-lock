package com.coditory.sherlock.sql

import com.zaxxer.hikari.HikariConfig

interface DataSourceConfigurer {
    void configure(HikariConfig config)
}
