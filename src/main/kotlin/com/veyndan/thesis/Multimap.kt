package com.veyndan.thesis

fun <K, V> MutableMap<K, MutableList<V>>.put(key: K, value: V) {
    getOrPut(key) { mutableListOf() } += value
}

operator fun <K, V> MutableMap<K, MutableList<V>>.set(key: K, value: V) {
    put(key, value)
}

fun <K, V> MutableMap<K, MutableList<V>>.remove(key: K, value: V) {
    getValue(key) -= value
    remove(key, mutableListOf())
}

fun <K, V> MutableMap<K, MutableList<V>>.replaceValues(key: K, values: MutableList<V>) {
    remove(key)
    put(key, values)
}
