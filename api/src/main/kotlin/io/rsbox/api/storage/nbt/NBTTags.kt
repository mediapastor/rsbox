package io.rsbox.api.storage.nbt

/**
 * @author Kyle Escobar
 */

sealed internal class TagBase(val type: Byte)

sealed internal class TagValue<out T>(type: Byte, val value: T) : TagBase(type) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TagValue<*>

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        return value?.hashCode() ?: 0
    }
}

internal class TagEnd : TagBase(0)
internal class TagByte(value: Byte) : TagValue<Byte>(1, value)
internal class TagShort(value: Short) : TagValue<Short>(2, value)
internal class TagInt(value: Int) : TagValue<Int>(3, value)
internal class TagLong(value: Long) : TagValue<Long>(4, value)
internal class TagFloat(value: Float) : TagValue<Float>(5, value)
internal class TagDouble(value: Double) : TagValue<Double>(6, value)
internal class TagByteArray(value: List<Byte>) : TagValue<List<Byte>>(7, value)
internal class TagString(value: String) : TagValue<String>(8, value)

internal class TagList(value: List<TagBase>) : TagValue<List<TagBase>>(9, value) {
    val dataType: Byte

    init {
        val vtypes = value.map { it.type }.toSet()
        dataType = when (vtypes.size) {
            0    -> 0
            1    -> vtypes.first()
            else -> error("Tag types must match!")
        }
    }
}

internal class TagCompound(value: Map<String, TagBase>) : TagValue<Map<String, TagBase>>(10, value)
internal class TagIntArray(value: List<Int>) : TagValue<List<Int>>(11, value)
internal class TagLongArray(value: List<Long>) : TagValue<List<Long>>(12, value)