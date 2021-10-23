package com.coditory.sherlock.base

import org.skyscreamer.jsonassert.JSONCompare
import org.skyscreamer.jsonassert.JSONCompareMode
import org.skyscreamer.jsonassert.JSONCompareResult

class JsonAssert {
    static boolean assertJsonEqual(String actual, String expected) {
        assertJsonEqualWithMode(actual, expected, JSONCompareMode.STRICT)
        return true
    }

    static boolean assertJsonLenientEqual(String actual, String expected) {
        assertJsonEqualWithMode(actual, expected, JSONCompareMode.LENIENT)
        return true
    }

    private static void assertJsonEqualWithMode(String actual, String expected, JSONCompareMode mode) {
        JSONCompareResult result = JSONCompare.compareJSON(expected, actual, mode)
        if (result.failed()) {
            String message = """
           |Error:
           |${result.getMessage()}
           |
           |Actual:
           |${actual}
           |
           |Expected:
           |${expected}
           """.stripMargin()
            assert false: message
        }
    }
}
