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

///**
// * Acting as a reshaper. Should rename it.
// */
//fun <T> List<List<T>>.zip(): List<List<T>> {
//    require(minBy { it.size } == maxBy { it.size })
//
//    val result = List<MutableList<T>>(first().size) { MutableList(10) {} }
//
//    forEachIndexed { i, it ->
//        it.forEachIndexed { j, it ->
//
//        }
//    }
//}

/**
 * Returns a list of lists, each built from elements of all lists with the same indexes.
 * Output has length of shortest input list.
 */
fun <T> zip(vararg lists: List<T>): List<List<T>> {
    return zip(*lists, transform = { it })
}

/**
 * Returns a list of values built from elements of all lists with same indexes using provided [transform].
 * Output has length of shortest input list.
 */
inline fun <T, V> zip(vararg lists: List<T>, transform: (List<T>) -> V): List<V> {
    val minSize = lists.map(List<T>::size).min() ?: return emptyList()
    val list = ArrayList<V>(minSize)

    val iterators = lists.map { it.iterator() }
    var i = 0
    while (i < minSize) {
        list.add(transform(iterators.map { it.next() }))
        i++
    }

    return list
}

fun Map<String, String>.mergeWith(another: Map<String, String>): Map<String, String> {
    val unionList: MutableMap<String, String> = toMutableMap()
    for ((key, value) in another) {
        unionList[key] = listOfNotNull(unionList[key], value).toSet().joinToString(", ")
    }
    return unionList
}

fun <K, V0, V1, V2> Map<K, V0>.mergeWith(other: Map<K, V1>, reduce: (V0, V1) -> V2): Map<K, V2> {
    val result = mutableMapOf<K, V2>()
    forEach { key, value ->
        result += key to reduce(value, other.getValue(key))
    }
    return result
}

fun <K, V> List<Map.Entry<K, V>>.toMap() = associate { it.toPair() }
