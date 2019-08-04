package com.coditory.sherlock.base

import groovy.transform.CompileStatic

@CompileStatic
class SpecSimulatedException extends RuntimeException {
  static throwSpecSimulatedException() {
    throw new SpecSimulatedException()
  }

  SpecSimulatedException() {
    this("Simulated exception for test")
  }

  SpecSimulatedException(String message) {
    super(message)
  }
}
