@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

package com.veyndan.thesis.utility

import com.veyndan.thesis.plus

fun <T> Sequence<T>.take(n: UInt): Sequence<T> = take(n.toInt())

infix fun <K, V1, V2> Map<K, V1>.zip(other: Map<K, V2>): Map<K, Pair<V1, V2>> = zip(other) { t1, t2 -> t1 to t2 }

fun <K, V1, V2, T> Map<K, V1>.zip(other: Map<K, V2>, transform: (a: V1, b: V2) -> T): Map<K, T> {
    require(keys == other.keys)

    return keys.map { key -> key to transform(getValue(key), other.getValue(key)) }.toMap()
}

fun <K, V> List<Map<K, V>>.toMultimap(): Map<K, List<V>> {
    val keys = first().keys

    forEach { require(it.keys == keys) }

    return fold(emptyMap()) { acc, map -> acc + map }
}

fun <K, V> Sequence<Map<K, V>>.toMultimap(): Map<K, List<V>> {
    val keys = first().keys

    forEach { require(it.keys == keys) }

    return fold(emptyMap()) { acc, map -> acc + map }
}
