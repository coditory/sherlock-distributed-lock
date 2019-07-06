package com.coditory.distributed.lock.tests.base

import groovy.json.JsonSlurper

class JsonAssert {
  private static slurper = new JsonSlurper()

  static void assertJsonEqual(String a, String b) {
    def mapA = slurper.parseText(a)
    def mapB = slurper.parseText(b)
    assert mapA == mapB
  }
}
