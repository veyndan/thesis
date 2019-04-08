@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

package com.veyndan.thesis

val Int.pounds: ULong
    get() = toULong().pounds

val Long.pounds: ULong
    get() = toULong().pounds

val ULong.pounds: ULong
    get() = this * 100uL
