package com.coditory.sherlock.base

import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.DynamicTest

fun runDynamicTest(name: String, testBody: suspend TestScope.() -> Unit): DynamicTest {
    return DynamicTest.dynamicTest(name) {
        runTest(testBody = testBody)
    }
}

fun <T : TestTuple> List<T>.runDynamicTest(testBody: suspend TestScope.(T) -> Unit): List<DynamicTest> {
    return this.map {
        DynamicTest.dynamicTest(it.name) {
            runTest {
                testBody(it)
            }
        }
    }
}
