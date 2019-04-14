@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

package com.veyndan.thesis.utility

fun <T> Sequence<T>.take(n: UInt): Sequence<T> = take(n.toInt())
