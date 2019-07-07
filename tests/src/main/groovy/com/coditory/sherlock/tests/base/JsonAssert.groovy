package com.coditory.sherlock.tests.base

import groovy.json.JsonSlurper

class JsonAssert {
  private static slurper = new JsonSlurper()

  static void assertJsonEqual(String actual, String expected) {
    def actualMap = slurper.parseText(actual)
    def expectedMap = slurper.parseText(expected)
    assert actualMap == expectedMap
  }
}
