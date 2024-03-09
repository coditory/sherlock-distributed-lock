package com.coditory.sherlock

import com.zaxxer.hikari.HikariConfig

interface DataSourceConfigurer {
    void configure(HikariConfig config)
}
