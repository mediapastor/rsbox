package io.rsbox.engine.serialization.nbt

/**
 * @author Kyle Escobar
 */

interface NBTView<in K, V> {
    operator fun get(key: K): V
    operator fun set(key: K, value: V)
}

fun <K, V> view(read: (key: K) -> V, write: (key: K, value: V) -> Unit): NBTView<K, V> = object :
    NBTView<K, V> {
    override fun get(key: K): V = read(key)
    override fun set(key: K, value: V) = write(key, value)
}