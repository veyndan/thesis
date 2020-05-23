package com.veyndan.thesis

import java.io.File

fun main() {
    File("scratch.txt").useLines { lines ->
        lines
            .filter { line -> line.startsWith("Bettor(id=Id(value=4)") }
            .map { line ->
                line
                    .removePrefix("Bettor(id=Id(value=4), funds=Pennies(value=1000), dryRunCount=20)={")
                    .removeSuffix("}")
            }
            .map { line ->
                val map = line.split(", ")
                    .map {
                        val (key, value) = it.split("=")
                        key.removePrefix("Competitor(").removeSuffix(")") to value
                    }
                    .toMap()

                map["487"]?.toDouble()?.run { 1.0 / this } ?: 0.0
            }
            .forEach(::println)
    }
}
