package io.rsbox.engine.model.storage.nbt

/**
 * @author Kyle Escobar
 */

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

inline fun <reified T> NBT.bind() = object : ReadWriteProperty<Any?, T?> {
    @Suppress("IMPLICIT_CAST_TO_ANY")
    override fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        val key = property.name
        return when (T::class) {
            Boolean::class -> bool[key]
            Byte::class    -> byte[key]
            Short::class   -> short[key]
            Int::class     -> int[key]
            Long::class    -> long[key]
            Float::class   -> float[key]
            Double::class  -> double[key]
            String::class  -> string[key]
            else           -> error("Don't know how to load type ${T::class} from NBT!")
        } as T?
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        val key = property.name
        when (T::class) {
            Boolean::class -> bool[key] = value as Boolean?
            Byte::class    -> byte[key] = value as Byte?
            Short::class   -> short[key] = value as Short?
            Int::class     -> int[key] = value as Int?
            Long::class    -> long[key] = value as Long?
            Float::class   -> float[key] = value as Float?
            Double::class  -> double[key] = value as Double?
            String::class  -> string[key] = value as String?
        }
    }
}

fun NBT.bindNBT() = object : ReadWriteProperty<Any?, NBT> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): NBT = this@bindNBT.nbt[property.name]

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: NBT) {
        this@bindNBT.nbt[property.name] = value
    }
}

inline fun <reified T> NBT.bindL() = object : ReadWriteProperty<Any?, List<T>?> {
    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: Any?, property: KProperty<*>): List<T>? {
        val key = property.name
        return when (T::class) {
            Boolean::class -> bools[key]
            Byte::class    -> bytes[key]
            Short::class   -> shorts[key]
            Int::class     -> ints[key]
            Long::class    -> longs[key]
            Float::class   -> floats[key]
            Double::class  -> doubles[key]
            String::class  -> strings[key]
            NBT::class     -> nbts[key]
            else           -> error("Don't know how to load a list of type ${T::class} from NBT!")
        } as List<T>?
    }

    @Suppress("UNCHECKED_CAST")
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: List<T>?) {
        val key = property.name
        when (T::class) {
            Boolean::class -> bools[key] = value as List<Boolean>?
            Byte::class    -> bytes[key] = value as List<Byte>?
            Short::class   -> shorts[key] = value as List<Short>?
            Int::class     -> ints[key] = value as List<Int>?
            Long::class    -> longs[key] = value as List<Long>?
            Float::class   -> floats[key] = value as List<Float>?
            Double::class  -> doubles[key] = value as List<Double>?
            String::class  -> strings[key] = value as List<String>?
            NBT::class     -> nbts[key] = value as List<NBT>?
        }
    }
}

infix fun <Ref, T : Any> ReadWriteProperty<Ref, T?>.default(value: T) = object : ReadWriteProperty<Ref, T> {
    override fun getValue(thisRef: Ref, property: KProperty<*>) =
        this@default.getValue(thisRef, property) ?: value

    override fun setValue(thisRef: Ref, property: KProperty<*>, value: T) {
        this@default.setValue(thisRef, property, value)
    }
}