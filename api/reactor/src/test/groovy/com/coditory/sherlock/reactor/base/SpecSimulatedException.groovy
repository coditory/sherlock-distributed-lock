package com.coditory.sherlock.reactor.base

class SpecSimulatedException extends RuntimeException {
  SpecSimulatedException() {
    this("Simulated exception for test")
  }

  SpecSimulatedException(String message) {
    super(message)
  }
}
