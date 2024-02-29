package com.coditory.sherlock.base

fun <T1> tuple(p1: T1) = Tuple(p1)

fun <T1, T2> tuple(
    p1: T1,
    p2: T2,
) = Tuple2(p1, p2)

fun <T1, T2, T3> tuple(
    p1: T1,
    p2: T2,
    p3: T3,
) = Tuple3(p1, p2, p3)

fun <T1, T2, T3, T4> tuple(
    p1: T1,
    p2: T2,
    p3: T3,
    p4: T4,
) = Tuple4(p1, p2, p3, p4)

fun <T1, T2, T3, T4, T5> tuple(
    p1: T1,
    p2: T2,
    p3: T3,
    p4: T4,
    p5: T5,
) = Tuple5(p1, p2, p3, p4, p5)

fun <T1, T2, T3, T4, T5, T6> tuple(
    p1: T1,
    p2: T2,
    p3: T3,
    p4: T4,
    p5: T5,
    p6: T6,
): Tuple6<T1, T2, T3, T4, T5, T6> {
    return Tuple6(p1, p2, p3, p4, p5, p6)
}

data class Tuple<T1>(val p1: T1)

data class Tuple2<T1, T2>(val p1: T1, val p2: T2)

data class Tuple3<T1, T2, T3>(val p1: T1, val p2: T2, val p3: T3)

data class Tuple4<T1, T2, T3, T4>(val p1: T1, val p2: T2, val p3: T3, val p4: T4)

data class Tuple5<T1, T2, T3, T4, T5>(val p1: T1, val p2: T2, val p3: T3, val p4: T4, val p5: T5)

data class Tuple6<T1, T2, T3, T4, T5, T6>(val p1: T1, val p2: T2, val p3: T3, val p4: T4, val p5: T5, val p6: T6)
