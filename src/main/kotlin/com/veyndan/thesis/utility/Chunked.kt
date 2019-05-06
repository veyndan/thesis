package com.veyndan.thesis.utility

fun <T> Iterable<T>.chunked(size: () -> Int): List<List<T>> {
    var rest = toList()

    val result = mutableListOf<List<T>>()

    while (true) {
        val size = size()

        if (rest.size < size) {
            break
        } else {
            result += rest.take(size)
            rest = rest.drop(size)
        }
    }

    return result
}
