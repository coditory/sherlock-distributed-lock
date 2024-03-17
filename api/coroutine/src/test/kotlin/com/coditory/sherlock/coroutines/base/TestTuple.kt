package com.coditory.sherlock.coroutines.base

fun testTuple(
    name: String,
    params: Map<String, Any>,
) = TestTupleMap(name, params)

fun <T1> testTuple(
    name: String,
    p1: T1,
) = TestTuple1(name, p1)

fun <T1, T2> testTuple(
    name: String,
    p1: T1,
    p2: T2,
) = TestTuple2(name, p1, p2)

fun <T1, T2, T3> testTuple(
    name: String,
    p1: T1,
    p2: T2,
    p3: T3,
) = TestTuple3(name, p1, p2, p3)

fun <T1, T2, T3, T4> testTuple(
    name: String,
    p1: T1,
    p2: T2,
    p3: T3,
    p4: T4,
) = TestTuple4(name, p1, p2, p3, p4)

fun <T1, T2, T3, T4, T5> testTuple(
    name: String,
    p1: T1,
    p2: T2,
    p3: T3,
    p4: T4,
    p5: T5,
) = TestTuple5(name, p1, p2, p3, p4, p5)

fun <T1, T2, T3, T4, T5, T6> testTuple(
    name: String,
    p1: T1,
    p2: T2,
    p3: T3,
    p4: T4,
    p5: T5,
    p6: T6,
) = TestTuple6(name, p1, p2, p3, p4, p5, p6)

interface TestTuple {
    val name: String
}

data class TestTupleMap(
    override val name: String,
    val params: Map<String, Any>,
) : TestTuple

data class TestTuple1<T1>(override val name: String, val p1: T1) : TestTuple {
    val first: T1 = p1
    val last: T1 = p1
}

data class TestTuple2<T1, T2>(override val name: String, val p1: T1, val p2: T2) : TestTuple {
    val first: T1 = p1
    val second: T2 = p2
    val last: T2 = p2
}

data class TestTuple3<T1, T2, T3>(override val name: String, val p1: T1, val p2: T2, val p3: T3) : TestTuple {
    val first: T1 = p1
    val second: T2 = p2
    val third: T3 = p3
    val last: T3 = p3
}

data class TestTuple4<T1, T2, T3, T4>(override val name: String, val p1: T1, val p2: T2, val p3: T3, val p4: T4) :
    TestTuple {
    val first: T1 = p1
    val second: T2 = p2
    val third: T3 = p3
    val last: T4 = p4
}

data class TestTuple5<T1, T2, T3, T4, T5>(
    override val name: String,
    val p1: T1,
    val p2: T2,
    val p3: T3,
    val p4: T4,
    val p5: T5,
) : TestTuple {
    val first: T1 = p1
    val second: T2 = p2
    val third: T3 = p3
    val last: T5 = p5
}

data class TestTuple6<T1, T2, T3, T4, T5, T6>(
    override val name: String,
    val p1: T1,
    val p2: T2,
    val p3: T3,
    val p4: T4,
    val p5: T5,
    val p6: T6,
) : TestTuple {
    val first: T1 = p1
    val second: T2 = p2
    val third: T3 = p3
    val last: T6 = p6
}
