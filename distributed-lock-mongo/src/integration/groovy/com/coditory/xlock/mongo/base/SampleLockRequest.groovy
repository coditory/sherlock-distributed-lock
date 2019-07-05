package com.coditory.xlock.mongo.base


import com.coditory.xlock.common.LockId

import com.coditory.xlock.common.InstanceId
import com.coditory.xlock.common.driver.LockRequest
import groovy.transform.CompileStatic

import java.time.Duration

@CompileStatic
class SampleLockRequest {
  private static final Map<String, ?> DEFAULT_PROPERTIES = [
      lockId           : LockId.of("sample-lock-id"),
      lockInstanceId   : LockInstanceId.of("sample-lock-instance-id"),
      serviceInstanceId: InstanceId.of("sample-service-instance-id"),
      duration         : Duration.ofMinutes(5)
  ]

  static LockRequest sampleLockRequest(Map<String, ?> customProperties = [:]) {
    Map<String, ?> properties = DEFAULT_PROPERTIES + customProperties
    return new LockRequest(
        properties.lockId as LockId,
        properties.lockInstanceId as LockInstanceId,
        properties.serviceInstanceId as InstanceId,
        properties.duration as Duration
    )
  }
}
