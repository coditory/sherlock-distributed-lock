package com.coditory.xlock.mongo

import groovy.transform.CompileStatic
import org.testcontainers.containers.GenericContainer
import org.testcontainers.spock.Testcontainers
import spock.lang.Shared
import spock.lang.Specification

@CompileStatic
@Testcontainers
abstract class MongoIntgSpec extends Specification {
  static final String locksDatabaseName = "mongo-xlock-test"
  static final String locksCollectionName = "locks"
  @Shared
  GenericContainer mongo = new GenericContainer<>("mongo:3.4") // deliberately using an older mongo version
      .withExposedPorts(27017)
}
