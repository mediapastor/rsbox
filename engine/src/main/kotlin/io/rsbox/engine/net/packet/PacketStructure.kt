package io.rsbox.engine.net.packet

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap

/**
 * @author Kyle Escobar
 */

data class PacketStructure(
    val type: PacketType,
    val opcodes: IntArray,
    val length: Int,
    val ignore: Boolean,
    val values: Object2ObjectLinkedOpenHashMap<String, PacketValue>
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PacketStructure

        if (type != other.type) return false
        if (!opcodes.contentEquals(other.opcodes)) return false
        if (length != other.length) return false
        if (ignore != other.ignore) return false
        if (values != other.values) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + opcodes.contentHashCode()
        result = 31 * result + length
        result = 31 * result + ignore.hashCode()
        result = 31 * result + values.hashCode()
        return result
    }

}