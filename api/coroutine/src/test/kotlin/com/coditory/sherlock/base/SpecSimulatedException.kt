package com.coditory.sherlock.base

class SpecSimulatedException : RuntimeException {
    constructor() : this(DEFAULT_MESSAGE)
    constructor(message: String) : super(message)

    @Synchronized
    override fun fillInStackTrace(): Throwable {
        return this
    }

    companion object {
        private const val DEFAULT_MESSAGE = "¯\\_(ツ)_/¯ Just a simulated exception"
    }
}
