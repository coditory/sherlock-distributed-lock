package com.coditory.sherlock.base

class SpecSimulatedException : RuntimeException {
    constructor() : this("Simulated exception for test")
    constructor(message: String) : super(message)
}
