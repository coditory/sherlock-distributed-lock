package com.coditory.sherlock.reactive.base

class SpecSimulatedException extends RuntimeException {
  SpecSimulatedException() {
    this("Simulated exception for test")
  }

  SpecSimulatedException(String message) {
    super(message)
  }
}
