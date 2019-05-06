@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

package com.veyndan.thesis.race

inline class Position(val value: UInt) : Comparable<Position> {

    operator fun plus(other: Position) = Position(value + other.value)

    override fun compareTo(other: Position) = value.compareTo(other.value)

    override fun toString() = "Position($value)"
}
