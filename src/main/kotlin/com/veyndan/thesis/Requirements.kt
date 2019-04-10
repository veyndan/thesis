@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

package com.veyndan.thesis

fun Double.requireMaxDecimalPlacesAllowed(numberOfDecimalPlaces: UInt, lazyMessage: () -> Any = {}) {
    require(toString().split(".").last().length.toUInt() <= numberOfDecimalPlaces, lazyMessage)
}
