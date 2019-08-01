package io.rsbox.api.net.packet

/**
 * @author Kyle Escobar
 */

abstract class PacketEncoder<T: Packet> {
    /**
     * Writes data from the [message] into [builder] based on the [structure].
     */
    fun encode(message: T, builder: GamePacketBuilder, structure: PacketStructure) {
        structure.values.values.forEach { value ->
            if (value.type != DataType.BYTES) {
                builder.put(value.type, value.order, value.transformation, extract(message, value.id))
            } else {
                builder.putBytes(extractBytes(message, value.id))
            }
        }
    }

    /**
     * Get the [Number] value based on the [key].
     */
    abstract fun extract(message: T, key: String): Number

    /**
     * Get the [ByteArray] value based on the [key].
     */
    abstract fun extractBytes(message: T, key: String): ByteArray
}