package com.coditory.distributed.lock.tests.base

import com.coditory.distributed.lock.common.InstanceId
import com.coditory.distributed.lock.common.LockId
import com.coditory.distributed.lock.common.LockRequest
import groovy.transform.CompileStatic

import java.time.Duration

@CompileStatic
class SampleLockRequest {
  private static final Map<String, ?> DEFAULT_PROPERTIES = [
      lockId    : LockId.of("sample-lock-id"),
      instanceId: InstanceId.of("sample-lock-instance-id"),
      duration  : Duration.ofMinutes(5)
  ]

  static LockRequest sampleLockRequest(Map<String, ?> customProperties = [:]) {
    Map<String, ?> properties = DEFAULT_PROPERTIES + customProperties
    return new LockRequest(
        properties.lockId as LockId,
        properties.instanceId as InstanceId,
        properties.duration as Duration
    )
  }
}
