@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

package com.veyndan.thesis

inline class Pennies(val value: ULong)

val Int.pounds: Pennies
    get() = toULong().pounds

val Long.pounds: Pennies
    get() = toULong().pounds

val ULong.pounds: Pennies
    get() = Pennies(this * 100U)
