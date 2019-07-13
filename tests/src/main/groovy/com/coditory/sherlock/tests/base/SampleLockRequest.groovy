package com.coditory.sherlock.tests.base


import com.coditory.sherlock.common.LockId
import com.coditory.sherlock.common.LockRequest
import com.coditory.sherlock.common.OwnerId
import groovy.transform.CompileStatic

import java.time.Duration

@CompileStatic
class SampleLockRequest {
  private static final Map<String, ?> DEFAULT_PROPERTIES = [
      lockId    : LockId.of("sample-acquire-id"),
      instanceId: OwnerId.of("sample-acquire-instance-id"),
      duration  : Duration.ofMinutes(5)
  ]

  static LockRequest sampleLockRequest(Map<String, ?> customProperties = [:]) {
    Map<String, ?> properties = DEFAULT_PROPERTIES + customProperties
    return new LockRequest(
        properties.lockId as LockId,
        properties.instanceId as OwnerId,
        properties.duration as Duration
    )
  }
}
